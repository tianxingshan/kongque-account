package com.kongque.service.account.impl;

import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.account.InvitationDto;
import com.kongque.dto.account.MembershipDetailDto;
import com.kongque.entity.Account;
import com.kongque.entity.Invitation;
import com.kongque.entity.ReferrerEvaluate;
import com.kongque.dto.account.ReferrerEvaluateDto;
import com.kongque.service.account.IReferrerEvaluateService;
import com.kongque.service.liaision.IMemebershipFeignService;
import com.kongque.util.*;
import net.sf.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ReferrerEvaluateServiceImpl implements IReferrerEvaluateService {

    private static Logger logger=LoggerFactory.getLogger(ReferrerEvaluateServiceImpl.class);

    @Resource
    private IDaoService daoService;

    @Resource
    private IMemebershipFeignService memebershipFeignService;

    @Override
    public Result getReferrerEvaluateList(PageBean pageBean, ReferrerEvaluateDto dto) {
        List<ReferrerEvaluate> data =new ArrayList<>();
        Result result = new Result();
        Criteria criteria = daoService.createCriteria(ReferrerEvaluate.class);
        criteria.createAlias("referrer","a",JoinType.LEFT_OUTER_JOIN);
        if (dto != null) {
            if (dto.getUserName()!=null && !dto.getUserName().equals("") && dto.getReferrerName() != null && !dto.getReferrerName().equals("")){//根据推荐人姓名和用户姓名模糊查询
                criteria.add(Restrictions.or(Restrictions.like("a.username",dto.getUserName(),MatchMode.ANYWHERE),Restrictions.like("a.username",dto.getReferrerName(),MatchMode.ANYWHERE)));
            }else if (dto.getUserName()!=null && !dto.getUserName().equals("")){//根据用户名称模糊查询
                criteria.add(Restrictions.like("a.username",dto.getUserName(),MatchMode.ANYWHERE));
            } else if (dto.getReferrerName() != null && !dto.getReferrerName().equals("")) {//根据推荐人姓名查询
                criteria.add(Restrictions.like("a.username",dto.getReferrerName(),MatchMode.ANYWHERE));
            }
        }
        if (pageBean.getRows() != null && pageBean.getPage() != null) {
            data=daoService.findListWithPagebeanCriteria(criteria,pageBean);
        }else {
            data=criteria.list();
        }
        //将账户表的权限设为null
        for (ReferrerEvaluate referrerEvaluate :data) {
            if (referrerEvaluate.getAppraiser() != null) {
                referrerEvaluate.getAppraiser().setRoleSet(null);
            }
            if (referrerEvaluate.getReferrer() != null) {
                referrerEvaluate.getReferrer().setRoleSet(null);
            }
        }
        result.setReturnData(data);
        result.setTotal(daoService.findTotalWithCriteria(criteria));
        return result;

    }


    @Override
    public Result getReferrerEvaluateList(PageBean pageBean) {
        Result result = new Result();
        Criteria criteria = daoService.createCriteria(ReferrerEvaluate.class);
        criteria.add(Restrictions.eq("appraiser.id",SysUtil.getAccountId()));
        List<ReferrerEvaluate> data=daoService.findListWithPagebeanCriteria(criteria,pageBean);
        //将账户表的权限设为null
        for (ReferrerEvaluate referrerEvaluate :data) {
            if (referrerEvaluate.getAppraiser() != null) {
                referrerEvaluate.getAppraiser().setRoleSet(null);
            }
            if (referrerEvaluate.getReferrer() != null) {
                referrerEvaluate.getReferrer().setRoleSet(null);
            }
        }
        result.setReturnData(data);
        result.setTotal(daoService.findTotalWithCriteria(criteria));
        return result;
    }

    @Override
    public Result saveReferrerEvaluate(ReferrerEvaluateDto  dto) {

            ReferrerEvaluate evaluate=new ReferrerEvaluate();
            //评价内容
            if (dto.getComment() != null && !dto.getComment().equals("")) {
                evaluate.setComment(dto.getComment());
            }
            //评价类型（用户投诉：“TS”，用户评价：“PJ”
            if (dto.getCommentType() != null && !"".equals(dto.getCommentType())) {
                evaluate.setCommentType(dto.getCommentType());
            }
            if (dto.getScore()!=null){
                evaluate.setScore(dto.getScore());
            }
            //当前登录人（评价人）
            Account account=new Account();
            account.setRoleSet(null);
            account.setId(SysUtil.getAccountId());
            evaluate.setAppraiser(account);
            //根据当前评价人查看推荐人信息
            Invitation invitation=new Invitation();
            invitation.setInviteeAccount(account);//被邀请人
            Criteria criteria =daoService.createCriteria(Invitation.class);
            criteria.createAlias("inviteeAccount","i");
            criteria.add(Restrictions.eq("inviteeAccount",account));
            criteria.add(Restrictions.eq("i.status","1"));//正常会员
            List<Invitation> list=criteria.list();
            if (null != list && list.size() > 0) {
                Invitation invitation1=list.get(0);
                Account referrer=new Account();
                referrer.setRoleSet(null);
                referrer.setId(invitation1.getInviterAccount().getId());
                evaluate.setReferrer(referrer);
                daoService.save(evaluate);
                return new Result(evaluate);
            }else {
                logger.info("根据登录人查不到推荐人不能进行推荐人评价功能，或者该会员账号被冻结！参数ReferrerEvaluateDto："+dto+"；当前登录用户信息Account："+account);
                return new Result(Constants.INVITATION.NO_INVATER,"当前用户没有推荐人，不能评价！或者该会员账号被冻结！");
            }

    }

    @Override
    public Result getReferrerEvaluateById(String referrerId) {
        ReferrerEvaluate referrerEvaluate=daoService.findById(ReferrerEvaluate.class,referrerId);
        if (referrerEvaluate.getReferrer() != null) {
            if (referrerEvaluate.getReferrer().getRoleSet() != null && referrerEvaluate.getReferrer().getRoleSet().size() > 0) {
                referrerEvaluate.getReferrer().setRoleSet(null);
            }
            if (referrerEvaluate.getAppraiser() != null) {
                if (referrerEvaluate.getAppraiser().getRoleSet() != null && referrerEvaluate.getAppraiser().getRoleSet().size() > 0) {
                    referrerEvaluate.getAppraiser().setRoleSet(null);
                }
            }
        }
        return new Result(referrerEvaluate);
    }

    @Override
    public Result getMembershipList(PageBean pageBean) {
        Map<String,Object> map = new HashMap<>();
        map.put("page",pageBean.getPage());
        map.put("rows",pageBean.getRows());
        Pagination<MembershipDetailDto> pagination = memebershipFeignService.getSalesLevelList(map);
        logger.info("PC修改推荐人，获取会员系统的销售列表信息接口调用完毕！");
        return new Result(pagination.getRows(),pagination.getTotal());
    }

    /**
     * 会员列表单独修改推荐人接口
     * 推荐人评价列表修改推荐人记录投诉内容通用接口
     * @param invitationDto
     * @return
     */
    @Override
    public Result updateInvitation(InvitationDto invitationDto) {
        if (invitationDto.getId() != null && !invitationDto.getId().equals("")) {//修改会员推荐表内容
            //修改邀请关系表中邀请人字段
            Criteria criteria=daoService.createCriteria(Invitation.class);
            //criteria.createAlias("inviterAccount","inviter");
            Account inviterAccount=new Account();
            inviterAccount.setId(invitationDto.getInviterId());
            criteria.add(Restrictions.eq("inviterAccount",inviterAccount));
            //criteria.createAlias("inviteeAccount","invitee");
            Account inviteeAccount =new Account();
            inviteeAccount.setId(invitationDto.getInviteeId());
            criteria.add(Restrictions.eq("inviteeAccount",inviteeAccount));
            List<Invitation> list=criteria.list();
            if (null != list && list.size() > 0) {
                Invitation oldInvitation= list.get(0);
                Account updateAccount=new Account();
                updateAccount.setRoleSet(null);
                updateAccount.setId(SysUtil.getAccountId());
                oldInvitation.setUpdateAccount(updateAccount);//修改人
                if (invitationDto.getInviterId()!=null && !invitationDto.getInviterId().equals("")){//修改推荐人
                    Account inviteAccount=new Account();
                    inviteAccount.setId(invitationDto.getReferrerId());
                    oldInvitation.setInviterAccount(inviteAccount);
                }
                if (invitationDto.getComplaintInfo() != null && !invitationDto.getComplaintInfo().equals("")) {
                    oldInvitation.setComplaintInfo(invitationDto.getComplaintInfo());
                }
                //获取邀请人会员信息
                Result result=memebershipFeignService.getMembershipInfo(invitationDto.getInviterId());

                if (Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())) {
                    JSONObject jsonObject=JSONObject.fromObject(result.getReturnData());
                    logger.info("PC请求修改后的推荐人信息："+JSONObject.fromObject(result.getReturnData()));
                    //查询出邀请人类型
                    oldInvitation.setInviterType(jsonObject.get("memberType").toString());
                }else {
                    return new Result(Constants.RESULT_CODE.ACCOUNT_EXCEPTION,"账号异常！");
                }
                //修改会员评价列表内容
                ReferrerEvaluate oldReferrerEvaluate=daoService.findById(ReferrerEvaluate.class,invitationDto.getId());
                if (invitationDto.getInviterId()!=null && !invitationDto.getInviterId().equals("")){//修改推荐人
                    Account referrer=new Account();
                    referrer.setId(invitationDto.getReferrerId());
                    oldReferrerEvaluate.setReferrer(referrer);
                }
                daoService.update(oldReferrerEvaluate);
                daoService.update(oldInvitation);
                result.setReturnData(oldReferrerEvaluate);
                return result;
            }else {
                return new Result("该邀请关系没有维护！");
            }
        }else {//修改推荐人表内容
            //修改邀请关系表中邀请人字段
            Criteria criteria=daoService.createCriteria(Invitation.class);
            //criteria.createAlias("inviterAccount","inviter");
            Account inviterAccount=new Account();
            inviterAccount.setId(invitationDto.getInviterId());
            criteria.add(Restrictions.eq("inviterAccount",inviterAccount));
            //criteria.createAlias("inviteeAccount","invitee");
            Account inviteeAccount =new Account();
            inviteeAccount.setId(invitationDto.getInviteeId());
            criteria.add(Restrictions.eq("inviteeAccount",inviteeAccount));
            List<Invitation> list=criteria.list();
            if (null != list && list.size() > 0) {
                Invitation oldInvitation= list.get(0);
                Account updateAccount=new Account();
                updateAccount.setRoleSet(null);
                updateAccount.setId(SysUtil.getAccountId());
                oldInvitation.setUpdateAccount(updateAccount);//修改人
                if (invitationDto.getInviterId()!=null && !invitationDto.getInviterId().equals("")){//修改推荐人
                    Account inviteAccount=new Account();
                    inviteAccount.setId(invitationDto.getReferrerId());
                    oldInvitation.setInviterAccount(inviteAccount);
                }
                if (invitationDto.getComplaintInfo() != null && !invitationDto.getComplaintInfo().equals("")) {
                    oldInvitation.setComplaintInfo(invitationDto.getComplaintInfo());
                }
                //获取邀请人会员信息
                Result result=memebershipFeignService.getMembershipInfo(invitationDto.getInviterId());
                if (Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())) {
                    JSONObject jsonObject=JSONObject.fromObject(result.getReturnData());
                    logger.info("PC请求修改后的推荐人信息："+JSONObject.fromObject(result.getReturnData()));
                    //查询出邀请人类型
                    oldInvitation.setInviterType(jsonObject.get("memberType").toString());
                }else {
                    return new Result(Constants.RESULT_CODE.ACCOUNT_EXCEPTION,"账号异常！");
                }
                daoService.update(oldInvitation);
                result.setReturnData(oldInvitation);
                return result;
            }
        }
       return new Result();
    }

    @Override
    public Result getInvitationList(PageBean pageBean, InvitationDto dto) {

        List<Invitation> data =new ArrayList<>();
        Criteria criteria = daoService.createCriteria(Invitation.class);
        if (pageBean.getRows() != null && pageBean.getPage() != null) {
            data=daoService.findListWithPagebeanCriteria(criteria,pageBean);
        }else {
            data=criteria.list();
        }
        Long total=daoService.findTotalWithCriteria(criteria);
        return new Result(data,total);
    }

    @Override
    public Result saveInvitation(InvitationDto dto) {
        Invitation invitation=new Invitation();
        //校验不能指定自己为推荐人
        if (dto.getInviteeId().equals(dto.getInviterId())){
            return new Result(Constants.INVITATION.INVITATION_NOT_INVITATION_MYSELF,"不能指定自己为推荐人，请重新选择推荐人！");
        }
        //保存推荐人
        if (dto.getInviterId() != null && !dto.getInviterId().equals("")) {
            Account inviter=new Account();
            inviter.setId(dto.getInviterId());
            invitation.setInviterAccount(inviter);

        }else {
            return new Result(Constants.INVITATION.INVITATION_INVITERID_NULL,"没有传邀请人id");
        }
        //保存被推荐会员信息
        if (dto.getInviteeId()!=null && !dto.getInviteeId().equals("")){
            Account invitee=new Account();
            invitee.setId(dto.getInviteeId());
            //校验当前用户是否已经被推荐
            Criteria criteria = daoService.createCriteria(Invitation.class);
            criteria.add(Restrictions.eq("inviteeAccount",invitee));
            List list=criteria.list();
            if (null!=list && list.size()>0) {
                return new Result(Constants.INVITATION.INVITATION_INVITEE_EXSIT,"该会员已经被推荐，请重新选择！") ;
            }else {
                invitation.setInviteeAccount(invitee);
            }
        }else {
            return new Result(Constants.INVITATION.INVITATION_INVITEE_NULL,"没有传被邀请人id");
        }
        invitation.setCreateTime(new Date());
        invitation.setInviterType(dto.getInviterType());
        daoService.save(invitation);
        return new Result(invitation);
    }
}

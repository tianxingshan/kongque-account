/**
* @author pengcheng
* @since 2018年5月4日
 */
package com.kongque.service.invitation;

import java.util.List;

import com.kongque.dto.account.InvitationDto;
import com.kongque.entity.Invitation;
import com.kongque.util.Result;

/**
 * @author pengcheng
 * @since 2018年5月4日
 */
public interface IInvitationService {
	
	/**
	 * 获取登录用户的邀请人账户名称
	 * @author pengcheng
	 * @since 2018年5月17日
	 */
	public Result  getInviterName();

	/**
	 * 添加邀请关系
	 * @author pengcheng
	 * @since 2018年5月4日
	 * @param inviteeId 受邀者平台账户id
	 * @param inviterId 邀请者平台账户id
	 * @return 新建邀请关系的id
	 */
    public String addInvitation(String inviteeId,String inviterId);
    
    /**
     * 检验指定的平台账户在系统中是否存在邀请关系
     * 
     * @author pengcheng
     * @since 2018年5月9日
     * @param inviteeId
     * @return
     */
    public Boolean checkInvitationFroInvitee(String inviteeId);
    
    /**
     * 获取被邀请人的邀请关系信息
     * 
     * @author pengcheng
     * @since 2018年5月8日
     * @param inviteeId
     * @return
     */
    public Invitation getInvitationForInvitee(String inviteeId);
    
    /**
     * 获取两级邀请关系信息
     * 返回list长度
     * @author yuehui
     * @since 2018年5月29日
     * @param inviteeId
     * @return
     */
    public Result getInvitationForInvitees(String inviteeId);
    
    /**
     * 获取属于指定邀请人的所有邀请关系列表
     * @author pengcheng
     * @since 2018年5月18日
     * @param inviterId 邀请人平台账户id
     * @return
     */
    public List<Invitation> getInvitationForInviter(String inviterId);

	/**
	 * 获取邀请人信息列表
	 *（用于会员系统显示邀请人账号业务）
	 * @author lilishan
	 * @since 2018年8月6日
	 * @return
	 */
   public Result getInviterListByIds(String[] acountIds);
   
   /**
    * 根据查询参数查询邀请关系
    * 
    * @author pengcheng
    * @since 2018年9月21日
    * @param queryDto
    * @return
    */
   public Result queryInvitation(InvitationDto queryDto);

	/**
	 * 根据推荐人id查询被推荐人id列表
	 *（用依品有调系统合伙人指派量体师业务）
	 * @author lilishan
	 * @since 2018年9月27日
	 * @return
	 */
	public Result getInviteeIdsByInviterId(String accountId);

	/**
	 * 根据被推荐人id查询推荐人账号名称
	 *（用依品有调系统合伙人指派量体师业务）
	 * @author lilishan
	 * @since 2018年9月29日
	 * @return
	 */
	public Result getInviterName(String accountId);

}

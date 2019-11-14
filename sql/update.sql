#yuehui 2018.1.11
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` text,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) DEFAULT '0',
  `trusted` tinyint(1) DEFAULT '0',
  `autoapprove` varchar(255) DEFAULT 'false',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('kongque_cloud', 'web-resource', 'kongque_cloud', 'read,write', 'password', null, 'ROLE_CLIENT', null, null, null, '2017-12-08 14:03:44', '0', '0', 'false');

-- yuehui 2018.1.11
DROP TABLE IF EXISTS `t_account_role`;
CREATE TABLE `t_account_role` (
  `c_account_id` varchar(40) NOT NULL COMMENT '关联账号表',
  `c_role_id` varchar(40) NOT NULL COMMENT '关联角色表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号角色表';

#yuehui 2018.1.11
DROP TABLE IF EXISTS `t_role_resource`;
CREATE TABLE `t_role_resource` (
  `c_role_id` varchar(40) NOT NULL,
  `c_resource_id` varchar(40) NOT NULL,
  PRIMARY KEY (`c_role_id`,`c_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

#yuehui 2018.1.11
DROP TABLE IF EXISTS `t_sys_resource`;
CREATE TABLE `t_sys_resource` (
  `c_id` varchar(40) NOT NULL,
  `c_description` text COMMENT 'url描述',
  `c_url_match` varchar(400) DEFAULT '' COMMENT 'url',
  `c_handle` varchar(4) DEFAULT '写' COMMENT '读、写',
  `c_father_id` varchar(40) DEFAULT NULL COMMENT '父级菜单',
  `c_father_ids` text COMMENT '各父级id用,隔开',
  `c_remarks` text COMMENT '信息备注',
  `c_type` varchar(40) DEFAULT '普通' COMMENT '资源类型：普通、菜单',
  `c_check` varchar(4) DEFAULT '是' COMMENT '是否校验',
  `c_source_order` int(11) DEFAULT '0' COMMENT '排序',
  `c_del` varchar(4) DEFAULT '否' COMMENT '是否删除',
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限url表';

#yuehui 2018.1.11
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `c_id` varchar(40) NOT NULL,
  `c_role_name` varchar(40) DEFAULT '' COMMENT '角色名',
  `c_remarks` text COMMENT '备注',
  `c_sys_id` varchar(40) DEFAULT NULL COMMENT '系统标识',
  `c_role_order` int(4) DEFAULT '1' COMMENT '排序',
  `c_business_id` varchar(40) DEFAULT '' COMMENT '商户id',
  `c_del` varchar(4) DEFAULT '否' COMMENT '是否删除',
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

alter table `t_account` modify column `c_token` CHAR(40) NULL DEFAULT NULL COMMENT '保存在Redis中的账号登录数据所对应的有效token';


#岳辉 2018/1/26 系统表

SET FOREIGN_KEY_CHECKS=0;

# v2.2 数据库修改开始

-- ----------------------------
-- Table structure for t_sys_info
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_info`;
CREATE TABLE `t_sys_info` (
  `c_id` varchar(40) NOT NULL,
  `c_lable` varchar(40) DEFAULT '' COMMENT '系统中文名',
  `c_value` varchar(40) DEFAULT '' COMMENT '系统code',
  `c_remarks` text COMMENT '备注',
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统信息表，修改时添加资源顶级节点';

-- ----------------------------
-- Records of t_sys_info
-- ----------------------------
INSERT INTO `t_sys_info` VALUES ('kongque-background', '孔雀云后台管理中心', 'kongque-background', null);
INSERT INTO `t_sys_info` VALUES ('kongque-cloud-platform', '孔雀云交易平台', 'kongque-cloud-platform', null);
INSERT INTO `t_sys_info` VALUES ('kongque-people', '孔雀人', 'kongque-people', null);
INSERT INTO `t_sys_info` VALUES ('kongque-tenant', '孔雀云商户系统', 'kongque-tenant', null);
SET FOREIGN_KEY_CHECKS=1;

-- 权限表添加系统id字段
ALTER TABLE `t_sys_resource`
ADD COLUMN `c_sys_id`  varchar(40) NULL DEFAULT NULL COMMENT '所属系统Id' AFTER `c_father_ids`;

-- 添加孔雀人系统资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
	('2c9180826120a59a016121e0a70d0018', '孔雀人', '', '写', NULL, NULL, 'kongque-people', NULL, '普通', '否', 2, '否');


-- ------------------------ 添加云平台系统角色 begin ------------------------

-- -----------------角色begin-----------------

-- 添加系统角色
INSERT INTO `t_sys_role` (`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_del`) VALUES
	('2c9180826140af420161410226f2000b', 'sys-tenant', NULL, 'kongque-cloud-platform', 1, '', '否'), -- 商户中心
	('2c9180826140af42016141028592000c', 'sys-selfrun', NULL, 'kongque-cloud-platform', 2, '', '否'), -- 自营中心
	('2c9180826131c1590161408dc7920002', 'sys-membership', NULL, 'kongque-cloud-platform', 0, '', '否'), -- 个人中心
	('35e5e2d9be83455da9e67c087dabd054', 'sys-background', NULL, 'kongque-cloud-platform', 3, '', '否'); -- 管理中心角色

-- 旧的账号统一设置成商户主账号
insert into t_account_role select t1.c_id ,'2c9180826140af420161410226f2000b' from t_account t1;

#岳辉 角色添加角色类型

ALTER TABLE `t_sys_role`
ADD COLUMN `c_role_type`  varchar(4) NULL DEFAULT '0' COMMENT '角色类型，0普通，1商户员工默认' AFTER `c_business_id`;

-- -- 给旧的商户添加商户默认角色
-- 系统管理员
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '系统管理员', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 店长
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '店长', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 加盟店
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '加盟店', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 店员
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '店员', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 财务
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '财务', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 仓库主管
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '仓库主管', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;
-- 售后服务部
INSERT INTO 
	`kongque-account`.`t_sys_role` 
				(`c_id`, `c_role_name`, `c_remarks`, `c_sys_id`, `c_role_order`, `c_business_id`, `c_role_type`, `c_del`)  
	select uuid(), '售后服务部', NULL, 'kongque-cloud-platform', 1, t1.c_id, '1', '否'
FROM 
	`kongque-tenant`.`t_tenant_detail` t1;

update `kongque-account`.`t_sys_role`  set `c_id` = replace(`c_id`, '-', '') where `c_id` like '%-%';

-- -----------------角色end-----------------




-- -----------------资源begin-----------------

-- 添加系统资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
	('2c9180826108916401611b9393f30001', '孔雀云交易平台', '', '写', NULL, NULL, 'kongque-cloud-platform', NULL, '菜单', '否', 1, '否');

-- 添加sys-tenant所属资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
	('2c9180826131c1590161409359b20004', '商户中心', '/businesscenter', '写', '2c9180826108916401611b9393f30001', '2c9180826108916401611b9393f30001', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	
	('2c918082621e2963016227607b3e00aa', '账户设置', 'menu:businesscenter:accountsettings', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c9180826131c159016140a7ab85000b', '商户详情', '/businesscenter/businessdetail', '写', '2c918082621e2963016227607b3e00aa', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227607b3e00aa', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808262414d390162414fea530000', '安全设置', '/businesscenter/businessSetting', '写', '2c918082621e2963016227607b3e00aa', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227607b3e00aa', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			('2c91808262414d390162416b859e000c', 'personpassword', '/businesscenter/personpassword', '写', '2c91808262414d390162414fea530000', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227607b3e00aa,2c91808262414d390162414fea530000', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c91808262414d390162416c18af000d', 'businessUpdatePhone', '/businesscenter/businessUpdatePhone', '写', '2c91808262414d390162414fea530000', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227607b3e00aa,2c91808262414d390162414fea530000', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			
	('2c918082621e2963016227623dfe00ab', '会员中心', 'menu:businesscenter:membercenter', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261daf99e0161e534c3560006', '会员管理', '/businesscenter/memberManagement', '写', '2c918082621e2963016227623dfe00ab', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227623dfe00ab', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c91808261f570820161ff3ea6420012', 'membershipDetail', '/businesscenter/membershipDetail', '写', '2c91808261daf99e0161e534c3560006', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e534c3560006', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261daf99e0161e5361f0b0008', '量体数据', '/businesscenter/volumeData', '写', '2c918082621e2963016227623dfe00ab', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e2963016227623dfe00ab', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			('2c91808261f570820161ff3fbc880013', 'volumeDataDetail', '/businesscenter/volumeDataDetail', '写', '2c91808261daf99e0161e5361f0b0008', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e5361f0b0008', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),

	('2c918082621e29630162276807ba00b6', '订单中心', 'menu:businesscenter:ordercenter', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		
		('2c91808261daf99e0161e536be170009', '普通订单', '/businesscenter/commonOrder', '写', '2c918082621e29630162276807ba00b6', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276807ba00b6', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c9180826217f2b20162198939650026', 'commonDetail', '/businesscenter/commonDetail', '写', '2c91808261daf99e0161e536be170009', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e536be170009', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	
		('2c91808261daf99e0161e5376bd1000a', '定制订单', '/businesscenter/customOrder', '写', '2c918082621e29630162276807ba00b6', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276807ba00b6', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			('2c91808261ff8fc2016203a4b4490001', 'customDetail', '/businesscenter/customDetail', '写', '2c91808261daf99e0161e5376bd1000a', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e5376bd1000a', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c918082621e29630162273a30b800a8', 'xiugaidingzhiorder', '/xiugaidingzhiorder', '写', '2c91808261daf99e0161e5376bd1000a', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e5376bd1000a', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	
		('2c91808261daf99e0161e537e470000b', '微调订单', '/businesscenter/adjustOrder', '写', '2c918082621e29630162276807ba00b6', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276807ba00b6', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
			('2c91808261f570820161ff40d6f50014', 'adjustOrderDetail', '/businesscenter/adjustOrderDetail', '写', '2c91808261daf99e0161e537e470000b', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e537e470000b', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		
		('2c9180826140af42016140b0860f0001', '采购单管理', '/businesscenter/businessselfcaigoudingdan', '写', '2c918082621e29630162276807ba00b6', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276807ba00b6', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
		('2c9180826131c159016140ae68bc0010', '供应单管理', '/businesscenter/selfpurchasebusinessdingdan', '写', '2c918082621e29630162276807ba00b6', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276807ba00b6', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '是'),

	('2c918082621e29630162276dfcc700b7', '结算中心', 'menu:businesscenter:settlementcenter', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
		('2c91808261daf99e0161e5386f40000c', '定制结算单', '/businesscenter/customSettlementSheet', '写', '2c918082621e29630162276dfcc700b7', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276dfcc700b7', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '是'),
		('2c91808261daf99e0161e538f5df000d', '微调结算单', '/businesscenter/trimSettlement', '写', '2c918082621e29630162276dfcc700b7', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276dfcc700b7', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '是'),
	
	('2c918082621e29630162276fa2eb00b8', '认证中心', 'menu:businesscenter:certificationcenter', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '否'),
		('2c9180826140af42016140f3ee5f0003', '成为工厂', '/businesscenter/businessapplyToBefactory', '写', '2c918082621e29630162276fa2eb00b8', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276fa2eb00b8', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c9180826140af42016140f269370002', '成为供应商', '/businesscenter/businessapplyToBeSupplier', '写', '2c918082621e29630162276fa2eb00b8', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276fa2eb00b8', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261daf99e0161e53efd9e000e', '成为品牌经销商', '/businesscenter/businessapplyToBeBrandDealer', '写', '2c918082621e29630162276fa2eb00b8', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e29630162276fa2eb00b8', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
	
	('2c918082621e296301622771bee000b9', '系统设置', 'menu:businesscenter:systemsettings', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 5, '否'),
		('2c9180826149a932016150b4b80f0003', '组织管理', '/businesscenter/businessManageDepartment', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c9180826140af4201614105a5e9000d', '员工管理', '/businesscenter/businessManageEmployee', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261daf99e0161e53fc2bd000f', '角色管理', '/businesscenter/businessManageRole', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		('2c91808261daf99e0161e540b56b0010', '分公司管理', '/businesscenter/companyList', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '否'),
			('2c9180826247e628016251e85df6005d', 'companyDetail', '/businesscenter/companyDetail', '写', '2c91808261daf99e0161e540b56b0010', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9,2c91808261daf99e0161e540b56b0010', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261daf99e0161e53565710007', '门店管理', '/businesscenter/shopManagement', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '否'),
			('2c91808261f570820161ff4473c00015', 'shopManagementDetail', '/businesscenter/shopManagementDetail', '写', '2c91808261daf99e0161e53565710007', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c91808261daf99e0161e53565710007', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c9180826131c159016140abedb4000d', '供应商品管理', '/businesscenter/businesssupplybusinesslist', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 5, '是'),
			('2c918082621e296301622359e5170047', 'supplyfabugoods_', '/supplyfabugoods_', '写', '2c9180826131c159016140abedb4000d', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140abedb4000d', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '是'),
			('2c918082621e2963016223c0b3a00052', 'supplyfabugoods', '/supplyfabugoods', '写', '2c9180826131c159016140abedb4000d', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140abedb4000d', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '是'),
			('2c918082621e2963016223c208410053', 'supplyfabugoodsa', '/supplyfabugoodsa', '写', '2c9180826131c159016140abedb4000d', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140abedb4000d', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '是'),
			('2c918082621e2963016223c39d120054', 'supplyfabugoodsb', '/supplyfabugoodsb', '写', '2c9180826131c159016140abedb4000d', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140abedb4000d', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
			('2c918082621e2963016223c51e020055', 'supplyfabugoodsc', '/supplyfabugoodsc', '写', '2c9180826131c159016140abedb4000d', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140abedb4000d', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '是'),
		('2c9180826131c159016140acd85f000e', '商品颜色管理', '/businesscenter/businesscolorcode', '写', '2c918082621e296301622771bee000b9', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c918082621e296301622771bee000b9', 'kongque-cloud-platform', NULL, '菜单', '是', 6, '是'),
	
	('2c91808261ff8fc20162037965830000', '微调', '/adjustMyOrder', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 6, '否'),
	
	-- 已删除:前端隐藏
	('2c9180826131c159016140aacdae000c', '商户商品管理', '/businesscenter/businesslist', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 7, '是'),
		('2c918082621e2963016223d26128005d', 'fabugoods_', '/fabugoods_', '写', '2c9180826131c159016140aacdae000c', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140aacdae000c', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '是'),
		('2c918082621e2963016223d39d50005e', 'fabugoods', '/fabugoods', '写', '2c9180826131c159016140aacdae000c', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140aacdae000c', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '是'),
		('2c918082621e2963016223d50ce1005f', 'fabugoodsa', '/fabugoodsa', '写', '2c9180826131c159016140aacdae000c', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140aacdae000c', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '是'),
		('2c918082621e2963016223d5f8300060', 'fabugoodsb', '/fabugoodsb', '写', '2c9180826131c159016140aacdae000c', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140aacdae000c', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '是'),
		('2c918082621e2963016223d7fed20061', 'fabugoodsc', '/fabugoodsc', '写', '2c9180826131c159016140aacdae000c', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004,2c9180826131c159016140aacdae000c', 'kongque-cloud-platform', NULL, '菜单', '是', 5, '是'),
	
	('2c9180826131c159016140adc2e2000f', '销售单管理', '/businesscenter/selfsalesbusinessdingdan', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 8, '是'),
	('2c9180826140af42016140b003f10000', '商品订单管理', '/businesscenter/businessdingdan', '写', '2c9180826131c1590161409359b20004', '2c9180826108916401611b9393f30001,2c9180826131c1590161409359b20004', 'kongque-cloud-platform', NULL, '菜单', '是', 9, '是');


INSERT INTO `t_role_resource` (`c_role_id`, `c_resource_id`) VALUES	
	('2c9180826140af420161410226f2000b', '2c9180826108916401611b9393f30001'), -- 孔雀云交易平台 系统资源
	('2c9180826140af420161410226f2000b', '2c9180826131c1590161409359b20004'), -- 商户中心
	('2c9180826140af420161410226f2000b', '2c918082621e2963016227607b3e00aa'),
	('2c9180826140af420161410226f2000b', '2c9180826131c159016140a7ab85000b'),
	('2c9180826140af420161410226f2000b', '2c91808262414d390162414fea530000'),
	('2c9180826140af420161410226f2000b', '2c91808262414d390162416b859e000c'),
	('2c9180826140af420161410226f2000b', '2c91808262414d390162416c18af000d'),
	('2c9180826140af420161410226f2000b', '2c918082621e2963016227623dfe00ab'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e534c3560006'),
	('2c9180826140af420161410226f2000b', '2c91808261f570820161ff3ea6420012'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e5361f0b0008'),
	('2c9180826140af420161410226f2000b', '2c91808261f570820161ff3fbc880013'),
	('2c9180826140af420161410226f2000b', '2c918082621e29630162276807ba00b6'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e536be170009'),
	('2c9180826140af420161410226f2000b', '2c9180826217f2b20162198939650026'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e5376bd1000a'),
	('2c9180826140af420161410226f2000b', '2c91808261ff8fc2016203a4b4490001'),
	('2c9180826140af420161410226f2000b', '2c918082621e29630162273a30b800a8'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e537e470000b'),
	('2c9180826140af420161410226f2000b', '2c91808261f570820161ff40d6f50014'),
	('2c9180826140af420161410226f2000b', '2c918082621e29630162276fa2eb00b8'),
	('2c9180826140af420161410226f2000b', '2c9180826140af42016140f3ee5f0003'),
	('2c9180826140af420161410226f2000b', '2c9180826140af42016140f269370002'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e53efd9e000e'),
	('2c9180826140af420161410226f2000b', '2c918082621e296301622771bee000b9'),
	('2c9180826140af420161410226f2000b', '2c9180826149a932016150b4b80f0003'),
	('2c9180826140af420161410226f2000b', '2c9180826140af4201614105a5e9000d'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e53fc2bd000f'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e540b56b0010'),
	('2c9180826140af420161410226f2000b', '2c9180826247e628016251e85df6005d'),
	('2c9180826140af420161410226f2000b', '2c91808261daf99e0161e53565710007'),
	('2c9180826140af420161410226f2000b', '2c91808261f570820161ff4473c00015'),
	('2c9180826140af420161410226f2000b', '2c91808261ff8fc20162037965830000');
	
	

-- 添加sys-selfrun所属资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
('2c9180826131c1590161409478950005', '自营中心', '/selfmanagecenter', '写', '2c9180826108916401611b9393f30001', '2c9180826108916401611b9393f30001', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
	('2c9180826227a786016227bc9bb70000', '商品管理中心', 'menu:selfmanagecenter:goodsmanagecenter', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f42297110004', '商户品类管理', '/selfmanagecenter/selfclassify', '写', '2c9180826227a786016227bc9bb70000', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227bc9bb70000', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f42304e40005', '商品管理', '/selfmanagecenter/putongshopmanage', '写', '2c9180826227a786016227bc9bb70000', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227bc9bb70000', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			('2c918082621e29630162233818920044', 'buildGeneralgoods', '/buildGeneralgoods', '写', '2c91808261e6c62f0161f42304e40005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c91808261e6c62f0161f42304e40005', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c918082621e296301622340ea3a0045', 'buildShopListModify', '/buildShopListModify', '写', '2c91808261e6c62f0161f42304e40005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c91808261e6c62f0161f42304e40005', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
			('2c918082621e29630162234f6bd40046', 'buildShopDetaillist', '/buildShopDetaillist', '写', '2c91808261e6c62f0161f42304e40005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c91808261e6c62f0161f42304e40005', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
			
	('2c9180826227a786016227be7be60001', '量体管理中心', 'menu:selfmanagecenter:volumemanagecenter', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261e6c62f0161f42370d60006', '量体部位管理', '/selfmanagecenter/volumeAttributeManagement', '写', '2c9180826227a786016227be7be60001', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227be7be60001', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f4240b130007', '品类量体管理', '/selfmanagecenter/categoryVolumeManagement', '写', '2c9180826227a786016227be7be60001', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227be7be60001', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	
	('2c9180826227a786016227c028f50002', '采购管理', 'menu:selfmanagecenter:purchasingmanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		('2c91808261e6c62f0161f42551f6000a', '采购清单管理', '/selfmanagecenter/purchaseListManagement', '写', '2c9180826227a786016227c028f50002', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c028f50002', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f425d8d9000b', '采购单管理', '/selfmanagecenter/purchaseOrderManagement', '写', '2c9180826227a786016227c028f50002', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c028f50002', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	
	('2c9180826227a786016227c219af0003', '供应商管理', 'menu:selfmanagecenter:suppliermanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '否'),
		('2c91808261e6c62f0161f428ca6b0011', '自采供应商管理', '/selfmanagecenter/selfcustomize', '写', '2c9180826227a786016227c219af0003', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c219af0003', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	
	('2c9180826227a786016227c494a30005', '销售订单管理', 'menu:selfmanagecenter:salesordermanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 5, '否'),
		('2c91808261e6c62f0161f4270b67000e', '普通订单', '/selfmanagecenter/selfCommonOrder', '写', '2c9180826227a786016227c494a30005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c494a30005', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c9180826208adc4016209af4a6f000c', '定制订单', '/selfmanagecenter/selfCustomOrder', '写', '2c9180826227a786016227c494a30005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c494a30005', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261e6c62f0161f42785fa000f', '加工订单', '/selfmanagecenter/processingOrder', '写', '2c9180826227a786016227c494a30005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c494a30005', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		('2c918082621e29630162230d6f7e0036', '微调订单管理', '/selfmanagecenter/adjustOrderStatus', '写', '2c9180826227a786016227c494a30005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c494a30005', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '否'),
			('2c918082622e0c0d01623d72884f002f', 'adjustOrderDetail', '/selfmanagecenter/adjustOrderDetail', '写', '2c918082621e29630162230d6f7e0036', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c494a30005,2c918082621e29630162230d6f7e0036', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			
	('2c9180826227a786016227c8f7d00006', '结算管理', 'menu:selfmanagecenter:settlementmanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 6, '否'),
		('2c91808261e6c62f0161f4263b4a000c', '定制结算单管理', '/selfmanagecenter/selfCustomSettlement', '写', '2c9180826227a786016227c8f7d00006', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c8f7d00006', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f426a3d6000d', '微调结算单管理', '/selfmanagecenter/selfTrimSettlement', '写', '2c9180826227a786016227c8f7d00006', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227c8f7d00006', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),

	('2c9180826227a786016227ca6ac80007', '系统管理', 'menu:selfmanagecenter:systemmanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 7, '否'),
		('2c91808261e6c62f0161f4b0e7fb0014', '组织管理', '/selfmanagecenter/selfbusinessManageDepartment', '写', '2c9180826227a786016227ca6ac80007', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227ca6ac80007', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261e6c62f0161f4b007680012', '员工管理', '/selfmanagecenter/selfbusinessManageEmployee', '写', '2c9180826227a786016227ca6ac80007', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227ca6ac80007', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261e6c62f0161f4b07a250013', '角色管理', '/selfmanagecenter/selfbusinessManageRole', '写', '2c9180826227a786016227ca6ac80007', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227ca6ac80007', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		('2c91808261e6c62f0161f4286a490010', '颜色管理', '/selfmanagecenter/selfcolor', '写', '2c9180826227a786016227ca6ac80007', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005,2c9180826227a786016227ca6ac80007', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
	
	-- 已删除:前端隐藏
	('2c9180826227a786016227c388b70004', '工厂管理', 'menu:selfmanagecenter:factorymanage', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '是'),
	('2c91808261e6c62f0161f4201dff0000', '商户详情', '/selfmanagecenter/selfbusinessdetail', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '是'),
	('2c91808261e6c62f0161f420cdb40001', '自营商品管理', '/selfmanagecenter/selfshoplist', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '是'),
	('2c91808261e6c62f0161f4219aaf0002', '商户品类管理', '/selfmanagecenter/commodityCategoryManagement', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '是'),
	('2c91808261e6c62f0161f42218b00003', '商户属性管理', '/selfmanagecenter/commodityPropertyManagement', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
	('2c91808261e6c62f0161f42478af0008', '销售订单管理', '/selfmanagecenter/selforderlist', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 8, '是'),
	('2c91808261e6c62f0161f424eac60009', '采购订单管理', '/selfmanagecenter/selfpurchaselist', '写', '2c9180826131c1590161409478950005', '2c9180826108916401611b9393f30001,2c9180826131c1590161409478950005', 'kongque-cloud-platform', NULL, '菜单', '是', 9, '是');


	
INSERT INTO `t_role_resource` (`c_role_id`, `c_resource_id`) VALUES
	('2c9180826140af42016141028592000c', '2c9180826108916401611b9393f30001'), -- 孔雀云交易平台 系统资源

	('2c9180826140af42016141028592000c', '2c9180826131c1590161409478950005'), -- 自营中心
	
	('2c9180826140af42016141028592000c', '2c9180826227a786016227bc9bb70000'),
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f42297110004'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f42304e40005'), 
	('2c9180826140af42016141028592000c', '2c918082621e29630162233818920044'), 
	('2c9180826140af42016141028592000c', '2c918082621e296301622340ea3a0045'), 
	('2c9180826140af42016141028592000c', '2c918082621e29630162234f6bd40046'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227be7be60001'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f42370d60006'),
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4240b130007'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227c028f50002'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f42551f6000a'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f425d8d9000b'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227c219af0003'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f428ca6b0011'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227c494a30005'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4270b67000e'), 
	('2c9180826140af42016141028592000c', '2c9180826208adc4016209af4a6f000c'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f42785fa000f'), 
	('2c9180826140af42016141028592000c', '2c918082621e29630162230d6f7e0036'), 
	('2c9180826140af42016141028592000c', '2c918082622e0c0d01623d72884f002f'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227c8f7d00006'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4263b4a000c'),
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f426a3d6000d'), 
	('2c9180826140af42016141028592000c', '2c9180826227a786016227ca6ac80007'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4b0e7fb0014'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4b007680012'), 
	('2c9180826140af42016141028592000c', '2c91808261e6c62f0161f4b07a250013');

-- 添加sys-membership所属资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
	('2c9180826131c15901614091f1aa0003', '个人中心', '/personcenter', '写', '2c9180826108916401611b9393f30001', '2c9180826108916401611b9393f30001', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	('2c9180826227a786016227f60a240008', '账户设置', 'menu:personcenter:accountsettings', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261f570820161f959f2690000', '个人详情', '/personcenter/persondetail', '写', '2c9180826227a786016227f60a240008', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f60a240008', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261f570820161f95af58a0002', '密码修改', '/personcenter/personpassword', '写', '2c9180826227a786016227f60a240008', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f60a240008', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),	
		('2c91808261f570820161ff3bb79d0011', '安全设置', '/personcenter/securitySetting', '写', '2c9180826227a786016227f60a240008', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f60a240008', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
			('2c91808261f570820161ff583e690016', 'updatePassword', '/personcenter/updatePassword', '写', '2c91808261f570820161ff3bb79d0011', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c91808261f570820161ff3bb79d0011', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c9180826208adc401620959ab600005', 'updatePhone', '/personcenter/updatePhone', '写', '2c91808261f570820161ff3bb79d0011', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c91808261f570820161ff3bb79d0011', 'kongque-cloud-platform', NULL, '菜单', '是', 9, '否'),

	('2c9180826227a786016227f793620009', '订单中心', 'menu:personcenter:ordercenter', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		('2c91808261f570820161f95c4ef30005', '普通订单', '/personcenter/putongdingdan', '写', '2c9180826227a786016227f793620009', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f793620009', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
			('2c91808261f570820161ff3216690010', 'putongdetail', '/personcenter/putongdetail', '写', '2c91808261f570820161f95c4ef30005', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c91808261f570820161f95c4ef30005', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),

	('2c9180826227a786016227f906e2000a', '认证中心', 'menu:personcenter:certificationcenter', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
		('2c91808261f570820161f95cc1730006', '技工认证', '/personcenter/technicalCertification', '写', '2c9180826227a786016227f906e2000a', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f906e2000a', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
		('2c91808261f570820161f95d2f800007', '设计师认证', '/personcenter/designerCertification', '写', '2c9180826227a786016227f906e2000a', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003,2c9180826227a786016227f906e2000a', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
		
	-- 已删除:前端隐藏
	('2c91808261f570820161f95a6a380001', '订单管理', '/personcenter/persondingdan', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '是'),
	('2c91808261f570820161f95b5f690003', '我的商户', '/personcenter/personbusiness', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '是'),
	('2c91808261f570820161f95bc3c40004', '订单管理', '/personcenter/selfpersondingdan', '写', '2c9180826131c15901614091f1aa0003', '2c9180826108916401611b9393f30001,2c9180826131c15901614091f1aa0003', 'kongque-cloud-platform', NULL, '菜单', '是', 4, '是');
	
INSERT INTO `t_role_resource` (`c_role_id`, `c_resource_id`) VALUES
	('2c9180826131c1590161408dc7920002', '2c9180826108916401611b9393f30001'), -- 孔雀云交易平台 系统资源

	('2c9180826131c1590161408dc7920002', '2c9180826131c15901614091f1aa0003'), -- 个人中心
	('2c9180826131c1590161408dc7920002', '2c9180826227a786016227f60a240008'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161f959f2690000'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161f95af58a0002'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161ff3bb79d0011'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161ff583e690016'), 
	('2c9180826131c1590161408dc7920002', '2c9180826208adc401620959ab600005'), 
	('2c9180826131c1590161408dc7920002', '2c9180826227a786016227f793620009'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161f95c4ef30005'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161ff3216690010'), 
	('2c9180826131c1590161408dc7920002', '2c9180826227a786016227f906e2000a'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161f95cc1730006'), 
	('2c9180826131c1590161408dc7920002', '2c91808261f570820161f95d2f800007');


-- 添加sys-background所属资源
INSERT INTO `t_sys_resource` (`c_id`, `c_description`, `c_url_match`, `c_handle`, `c_father_id`, `c_father_ids`, `c_sys_id`, `c_remarks`, `c_type`, `c_check`, `c_source_order`, `c_del`) VALUES
	('2c9180826208adc4016209726f2e0006', '管理中心', '/backgroundcenter', '写', '2c9180826108916401611b9393f30001', '2c9180826108916401611b9393f30001', 'kongque-cloud-platform', NULL, '菜单', '是', 3, '否'),
	
	('4028800762089e3301620991893e0003', '商户管理', '', '写', '2c9180826208adc4016209726f2e0006', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	('4028800762089e33016209928dba0004', 'main', '/main', '写', '4028800762089e3301620991893e0003', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e3301620991893e0003', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	('4028800762089e33016209932f640005', '商户账号列表', '/TenantAccount', '写', '4028800762089e3301620991893e0003', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e3301620991893e0003', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	('4028800762089e3301620993b0660006', '商户详情列表', '/TenantDetails', '写', '4028800762089e3301620991893e0003', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e3301620991893e0003', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
	
	('4028800762089e330162099482260007', '会员管理', '', '写', '2c9180826208adc4016209726f2e0006', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	('4028800762089e3301620994ff380008', '会员列表', '/Member', '写', '4028800762089e330162099482260007', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e330162099482260007', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	('4028800762089e3301620996ef690009', '技工认证', '/TechnicalCertification', '写', '4028800762089e330162099482260007', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e330162099482260007', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	('4028800762089e33016209976e14000a', '设计师认证', '/DesignerCertification', '写', '4028800762089e330162099482260007', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e330162099482260007', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
	
	('4028800762089e33016209980275000b', '系统管理', '', '写', '2c9180826208adc4016209726f2e0006', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否'),
	('4028800762089e33016209987685000c', '权限资源管理', '/sysResource', '写', '4028800762089e33016209980275000b', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e33016209980275000b', 'kongque-cloud-platform', NULL, '菜单', '是', 0, '否'),
	('4028800762089e33016209994abf000d', '角色权限管理', '/sysRole', '写', '4028800762089e33016209980275000b', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e33016209980275000b', 'kongque-cloud-platform', NULL, '菜单', '是', 1, '否'),
	('4028800762089e3301620999c849000e', '用户角色管理', '/accountRole', '写', '4028800762089e33016209980275000b', '2c9180826108916401611b9393f30001,2c9180826208adc4016209726f2e0006,4028800762089e33016209980275000b', 'kongque-cloud-platform', NULL, '菜单', '是', 2, '否');

INSERT INTO `t_role_resource` (`c_role_id`, `c_resource_id`) VALUES
	('35e5e2d9be83455da9e67c087dabd054', '2c9180826208adc4016209726f2e0006'), -- 管理中心
	
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e3301620991893e0003'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209928dba0004'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209932f640005'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e3301620993b0660006'),
	
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e330162099482260007'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e3301620994ff380008'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e3301620996ef690009'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209976e14000a'),
	
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209980275000b'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209987685000c'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e33016209994abf000d'),
	('35e5e2d9be83455da9e67c087dabd054', '4028800762089e3301620999c849000e')
	; 
	
-- -----------------资源end-----------------

-- ------------------------ 添加云平台系统角色 end ------------------------




-- sysId 数据处理
UPDATE t_sys_resource set  c_sys_id ='kongque-people' where  POSITION('2c9180826120a59a016121e0a70d0018' IN c_father_ids) >0;
update t_sys_resource set  c_sys_id ='kongque-people' where c_id='2c9180826120a59a016121e0a70d0018';

UPDATE t_sys_resource set  c_sys_id ='kongque-cloud-platform' where  POSITION('2c9180826108916401611b9393f30001' IN c_father_ids) >0;
update t_sys_resource set  c_sys_id ='kongque-cloud-platform' where c_id='2c9180826108916401611b9393f30001';
update t_sys_resource set  c_description ='孔雀云交易平台' where c_id='2c9180826108916401611b9393f30001';

-- 部门表
CREATE TABLE `t_department` (
  `c_id` varchar(32) NOT NULL,
  `c_name` varchar(255) NOT NULL COMMENT '名称',
  `c_path_ids` varchar(1000) COMMENT '所有父id路径排列，使用英文逗号隔开',
  `c_parent_id` varchar(32) COMMENT '所属父id',
  `c_sys_id` varchar(255) NOT NULL COMMENT '系统标识',
  `c_business_id` varchar(40) COMMENT '业务id',
  `c_remarks` varchar(1000) COMMENT '备注',
  `c_create_time` DATETIME NOT NULL COMMENT '创建日期',
  `c_update_time` DATETIME COMMENT '修改日期',
  `c_creator_id` VARCHAR(32) NOT NULL COMMENT '创建人（账号系统账号id）',
  `c_last_modifier` VARCHAR(32) COMMENT '最后修改人（账号系统账号id）',
  `c_del` varchar(4) NOT NULL DEFAULT '0' COMMENT '状态：0：正常、1：删除',
  PRIMARY KEY (`c_id`),
  KEY `index_department_parent_id` (`c_parent_id`),
  KEY `index_department_creator_id` (`c_creator_id`),
  KEY `index_department_last_modifier` (`c_last_modifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门表';


-- 新建短信验证表
CREATE TABLE `t_sms_verification` (
	`c_id` VARCHAR(32) NOT NULL,
	`c_phone` VARCHAR(255) NOT NULL COMMENT '手机号',
	`c_verification` VARCHAR(255) NOT NULL COMMENT '验证码',
	`c_create_time` DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY (`c_id`)
)
COMMENT='短信验证' COLLATE='utf8_general_ci' ENGINE=InnoDB;

-- account表中添加手机号字段
ALTER TABLE `t_account`
ADD COLUMN `c_phone` VARCHAR(255) NULL DEFAULT '' COMMENT '当前账户手机号' AFTER `c_password`;

-- t_account_role
INSERT INTO `t_account_role`(`c_account_id`,`c_role_id`) VALUES('4f57ee7aba1411e7851700163e13ed18','35e5e2d9be83455da9e67c087dabd054');

INSERT INTO `t_account_role`(`c_account_id`,`c_role_id`) VALUES('2c91808360f923c0016107399fe30022','2c9180826140af42016141028592000c');

INSERT INTO `t_account_sys` VALUES('99cb7db1e8fa4ae78b00362c2cdd986c','2c91808360f923c0016107399fe30022','kongque-background',NULL);

#v2.2 数据库修改结束

INSERT INTO `t_sys_info` VALUES ('xingyu-order', '星域接单系统', 'xingyu-order', null);

#v2.3修改

ALTER TABLE t_account ADD COLUMN c_openid VARCHAR(255) NULL DEFAULT '' COMMENT '与当前账户的openid' AFTER c_phone;

ALTER TABLE t_account ADD COLUMN c_unionid VARCHAR(255) NULL DEFAULT '' COMMENT '与当前账户的unionid' AFTER c_openid;

CREATE TABLE `t_invitation` (
	`c_id` VARCHAR(32) NOT NULL PRIMARY KEY,
	`c_invitee_id` VARCHAR(32) NOT NULL COMMENT '被邀请者平台账户id',
	`c_inviter_id` VARCHAR(32) NOT NULL COMMENT '邀请者平台账户id',
	`c_create_time` DATETIME NOT NULL COMMENT '创建时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邀请关系表';

ALTER TABLE t_account ADD COLUMN c_source VARCHAR(4) DEFAULT '1' NOT NULL COMMENT '平台账户注册来源：[0]非应用系统后端创建[1]PC端[2]App端[3]小程序[31]小程序注册的普通用户' AFTER c_unionid;

ALTER TABLE t_account ADD COLUMN c_mapped_id VARCHAR(32) DEFAULT NULL COMMENT '与当前账户（通常是普通小程序的自动注册账户）有映射关系的普通类型账户的id' AFTER c_status;

ALTER TABLE t_account ADD COLUMN c_invitation_code VARCHAR(255) NOT  NULL COMMENT '当前账户的邀请码' AFTER c_mapped_id;

ALTER TABLE t_account ADD COLUMN c_sys_id VARCHAR(255) NOT NULL DEFAULT '' COMMENT '账户注册来源业务系统id' AFTER c_source;
/*添加衣品有调业务系统信息*/
insert into t_sys_info(c_id,c_lable,c_value) values('yipinyoudiao-applet','衣品有调','yipinyoudiao-applet');
/*更新账户现有数据的sys_id字段*/
update t_account set c_sys_id=(select c_id from t_sys_info where c_lable='衣品有调') where c_source='3' or c_source='31';
update t_account set c_sys_id=(select c_id from t_sys_info where c_lable='孔雀云交易平台') where c_source!='3' and c_source!='31';


#v2.3 結束


# 分润 v2.4.1


/**
  lilishan
  创建推荐人评价表
  2018.7.13
 */
CREATE TABLE IF NOT EXISTS `kongque-account`.`t_referrer_evaluate` (
  `c_id` varchar(50) NOT NULL COMMENT '主键',
  `c_referrer_id` varchar(50) DEFAULT NULL COMMENT '推荐人id',
  `c_appraiser_id` varchar(50) DEFAULT NULL COMMENT '评价人id',
  `c_comment` text COMMENT '评价内容'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推荐人评价表';

/**
  lilishan
  邀请关系表，新增修改人id、邀请类型、投诉内容字段
  2018.7.16
 */
ALTER TABLE `t_invitation`
	ADD COLUMN `c_update_account_id` VARCHAR(50) NOT NULL COMMENT '修改人id' AFTER `c_create_time`,
	ADD COLUMN `c_inviter_type` VARCHAR(50) NOT NULL COMMENT '邀请类型（普通：PT、销售：XS、合伙人：HHR）' AFTER `c_update_account_id`,
	ADD COLUMN `c_complaint_info` TEXT NULL COMMENT '投诉内容' AFTER `c_inviter_type`;

/**
lilishan
用户评价表新增评价类型标识字段
2018.7.30
 */
ALTER TABLE `t_referrer_evaluate`
	ADD COLUMN `c_comment_type` VARCHAR(50) NULL COMMENT '评价类型（用户投诉：“TS”，用户评价：“PJ”）' AFTER `c_comment`;


#2.4.1 结束

#2.4.2 开始

/**
lilishan
用户评价表新增评价分数字段
2018.8.23
 */
ALTER TABLE `t_referrer_evaluate`
	ADD COLUMN `c_score` DECIMAL(10,2) NULL COMMENT '用户评分' AFTER `c_comment_type`;

# 2.4.3 无sql改动

# v3.0 开始

# 账号表
ALTER TABLE `t_account`
ADD COLUMN `c_new_flag`  varchar(4) NULL DEFAULT '0' COMMENT '新手标识 0是 1否' AFTER `c_token`,
ADD COLUMN `c_message_flag`  varchar(4) NULL DEFAULT '2' COMMENT '企业账号完善信息标识 0未完善 1已完善 2非企业账号不必校验' AFTER `c_new_flag`;

# v3.0 结束
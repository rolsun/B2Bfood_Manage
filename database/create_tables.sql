-- 创建数据库
CREATE DATABASE IF NOT EXISTS `B2B_food-manage` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `B2B_food-manage`;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `usertype` INT(1) DEFAULT NULL COMMENT '用户类型: 1-采购商, 2-供应商, 3-管理员',
  `status` INT(1) DEFAULT '1' COMMENT '用户状态: 1-正常, 2-禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `roles` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `permissions` JSON DEFAULT NULL COMMENT '权限JSON数组',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 插入默认管理员角色
INSERT INTO `roles` (`id`, `role_name`, `description`, `permissions`) VALUES
(1, '超级管理员', '系统超级管理员，拥有所有权限', '["*"]');

-- 插入默认管理员账号（密码为admin123）
INSERT INTO `users` (`id`, `username`, `password`, `usertype`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 3, 1);

-- 创建商品分类表（需要在products表之前创建，因为products有外键引用）
CREATE TABLE IF NOT EXISTS `categories` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` INT(11) DEFAULT '0' COMMENT '父分类ID',
  `level` INT(1) DEFAULT '1' COMMENT '分类级别',
  `sort` INT(11) DEFAULT '0' COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 创建钱包表
CREATE TABLE IF NOT EXISTS `wallets` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL COMMENT '所属用户ID',
  `balance` DECIMAL(10,2) DEFAULT '0.00' COMMENT '当前余额(元)',
  `status` INT(1) DEFAULT '1' COMMENT '钱包状态: 1-正常, 2-冻结',
  `last_update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `wallets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

-- 创建交易记录表
CREATE TABLE IF NOT EXISTS `transaction_records` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `wallet_id` INT(11) NOT NULL COMMENT '所属钱包ID',
  `type` INT(1) NOT NULL COMMENT '交易类型: 1-充值, 2-提现, 3-支付',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '交易金额(元)',
  `balance_after` DECIMAL(10,2) NOT NULL COMMENT '交易后余额(元)',
  `payment_method` VARCHAR(50) DEFAULT NULL COMMENT '支付方式(仅充值有此参数)',
  `bank_card_number` VARCHAR(50) DEFAULT NULL COMMENT '银行卡号(仅提现有此参数)',
  `payee_id` INT(11) DEFAULT NULL COMMENT '收款方ID(仅支付有此参数)',
  `transaction_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '交易备注',
  PRIMARY KEY (`id`),
  KEY `wallet_id` (`wallet_id`),
  CONSTRAINT `transaction_records_ibfk_1` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 创建售后申请表
CREATE TABLE IF NOT EXISTS `after_sales` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `order_id` INT(11) DEFAULT NULL COMMENT '订单ID',
  `buyer_id` INT(11) NOT NULL COMMENT '采购商ID',
  `seller_id` INT(11) NOT NULL COMMENT '供应商ID',
  `after_sales_type` INT(1) NOT NULL COMMENT '售后类型: 1-退货退款, 2-仅退款, 3-换货',
  `status` INT(1) NOT NULL COMMENT '售后状态: 1-待审核,2-审核通过,3-审核拒绝,4-待退货,5-待收货,6-已完成,7-已关闭',
  `reason` VARCHAR(100) NOT NULL COMMENT '申请原因',
  `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '申请退款金额',
  `review_comment` TEXT DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`id`),
  KEY `buyer_id` (`buyer_id`),
  KEY `seller_id` (`seller_id`),
  CONSTRAINT `after_sales_ibfk_1` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `after_sales_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='售后申请表';

-- 创建售后操作日志表
CREATE TABLE IF NOT EXISTS `after_sales_logs` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `after_sales_id` INT(11) NOT NULL COMMENT '售后申请ID',
  `operator_id` INT(11) NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人名称',
  `operation_content` TEXT NOT NULL COMMENT '操作内容',
  `operation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `after_sales_id` (`after_sales_id`),
  CONSTRAINT `after_sales_logs_ibfk_1` FOREIGN KEY (`after_sales_id`) REFERENCES `after_sales` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='售后操作日志表';

-- 创建商品表（categories表需要在products表之前创建）
CREATE TABLE IF NOT EXISTS `products` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `category_id` INT(11) NOT NULL COMMENT '分类ID',
  `seller_id` INT(11) NOT NULL COMMENT '供应商ID',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `stock` INT(11) NOT NULL DEFAULT '0' COMMENT '库存数量',
  `description` TEXT DEFAULT NULL COMMENT '商品描述',
  `images` JSON DEFAULT NULL COMMENT '商品图片URL数组',
  `status` INT(1) DEFAULT '1' COMMENT '商品状态: 1-上架, 2-下架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `seller_id` (`seller_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

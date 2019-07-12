DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NULL,
  `userId` INT NOT NULL,
  `createdDate` DATETIME NOT NULL,
  `commentCount` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `date_index` (`createdDate` ASC));

  DROP TABLE IF EXISTS `user`;
  CREATE TABLE `user` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL DEFAULT '',
    `password` varchar(128) NOT NULL DEFAULT '',
    `salt` varchar(32) NOT NULL DEFAULT '',
    `headUrl` varchar(256) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  DROP TABLE IF EXISTS `login_ticket`;
  CREATE TABLE `login_ticket` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `userId` INT NOT NULL,
    `ticket` VARCHAR(45) NOT NULL,
    `expired` DATETIME NOT NULL,
    `status` INT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `ticket_UNIQUE` (`ticket` ASC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  DROP TABLE IF EXISTS `comment`;
  CREATE TABLE `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `userId` INT NOT NULL,
  `entityId` INT NOT NULL,
  `entityType` INT NOT NULL,
  `createdDate` DATETIME NOT NULL,
  `status` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `entity_index` (`entityId` ASC, `entityType` ASC)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  DROP TABLE IF EXISTS `message`;
  CREATE TABLE `message` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `fromId` INT NULL,
    `toId` INT NULL,
    `content` TEXT NULL,
    `createdDate` DATETIME NULL,
    `hasRead` INT NULL,
    `conversationId` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `conversation_index` (`conversationId` ASC),
    INDEX `created_date` (`createdDate` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

  DROP TABLE IF EXISTS `feed`;
  CREATE TABLE `feed` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `createdDate` DATETIME NULL,
    `userId` INT NULL,
    `data` TINYTEXT NULL,
    `type` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `user_index` (`userId` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

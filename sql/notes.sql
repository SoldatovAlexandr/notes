DROP DATABASE IF EXISTS notes;
CREATE DATABASE `notes`;
USE `notes`;

CREATE TABLE user
(
    id         INT(11)     NOT NULL AUTO_INCREMENT,
    login      VARCHAR(50) NOT NULL,
    password   VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    patronymic VARCHAR(50) DEFAULT NULL,
    deleted    BOOLEAN     DEFAULT FALSE,
    type       VARCHAR(10) DEFAULT FALSE,
    UNIQUE KEY (login),
    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE session
(
    id          INT(11)     NOT NULL AUTO_INCREMENT,
    user_id     INT(11)     NOT NULL,
    token       VARCHAR(50) NOT NULL,
    last_action DATETIME    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE section
(
    id      INT(11)     NOT NULL AUTO_INCREMENT,
    user_id INT(11)     NOT NULL,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (name),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE note
(
    id         INT(11) NOT NULL AUTO_INCREMENT,
    user_id    INT(11) NOT NULL,
    section_id INT(11) NOT NULL,
	subject    VARCHAR(50) NOT NULL,
    created    DATETIME    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE cascade,
    FOREIGN KEY (section_id) REFERENCES section (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE rating
(
    user_id INT(11) NOT NULL,
    note_id INT(11) NOT NULL,
    number INT(11) NOT NULL,
    PRIMARY KEY (user_id, note_id),
    UNIQUE (user_id, note_id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE cascade,
    FOREIGN KEY (note_id) REFERENCES note (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE note_version
(
    note_id         INT(11)      NOT NULL,
    revision_id     INT(11)      NOT NULL,
    body            VARCHAR(100) NOT NULL,
    PRIMARY KEY (note_id, revision_id),
    UNIQUE (note_id, revision_id),
    FOREIGN KEY (note_id) REFERENCES note (id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;


CREATE TABLE comment
(
    id                   INT(11)      NOT NULL AUTO_INCREMENT,
    user_id              INT(11)      NOT NULL,
    note_id              INT(11)      NOT NULL,
    revision_id          INT(11)      NOT NULL,
    created              DATETIME     NOT NULL,
    body                 VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE cascade,
    FOREIGN KEY (note_id ,revision_id) REFERENCES note_version (note_id ,revision_id) ON DELETE cascade
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE following
(
    follower_id  INT(11) NOT NULL,
    following_id INT(11) NOT NULL,
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE cascade,
    FOREIGN KEY (following_id) REFERENCES user (id) ON DELETE cascade,
    UNIQUE KEY (follower_id, following_id),
    PRIMARY KEY (follower_id, following_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

CREATE TABLE ignoring
(
    ignore_id    INT(11) NOT NULL,
    ignore_by_id INT(11) NOT NULL,
    FOREIGN KEY (ignore_id) REFERENCES user (id) ON DELETE cascade,
    FOREIGN KEY (ignore_by_id) REFERENCES user (id) ON DELETE cascade,
    UNIQUE KEY (ignore_id, ignore_by_id),
    PRIMARY KEY (ignore_id, ignore_by_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;

INSERT INTO user (first_name, last_name, patronymic, login, password, type)
VALUES("admin", "admin", "admin", "admin", "password", "SUPER_USER");
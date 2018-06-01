DROP TABLE `authors`;
DROP TABLE `categories`;
DROP TABLE `main`;


CREATE TABLE `main` (
  `id` varchar(30) NOT NULL,
  `title` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `primary_category` varchar(20) NOT NULL,
  `ref` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `authors` (
  `article_id` varchar(30) NOT NULL,
  `name` varchar(255) NOT NULL,
  KEY `article_id` (`article_id`),
  CONSTRAINT `authors_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `main` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `categories` (
  `article_id` varchar(30) NOT NULL,
  `category` varchar(255) NOT NULL,
  KEY `article_id` (`article_id`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `main` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
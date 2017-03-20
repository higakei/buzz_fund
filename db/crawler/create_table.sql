-- MySQL dump 10.11
--
-- Host: localhost    Database: Nikkei
-- ------------------------------------------------------
-- Server version	5.0.67-modified

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `toshiblogs`
--

DROP TABLE IF EXISTS `toshiblogs`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `toshiblogs` (
  `id` int(11) NOT NULL auto_increment,
  `url` varchar(255) default NULL,
  `authorId` varchar(255) default NULL,
  `title` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `blogfeedRDF`
--

DROP TABLE IF EXISTS `blogfeedRDF`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `blogfeedRDF` (
  `documentId` varchar(128) NOT NULL,
  `authorId` varchar(255) default NULL,
  `url` text,
  `title` varchar(255) default NULL,
  `body` mediumtext,
  `description` text,
  `date` datetime default NULL,
  `dateindex` date default NULL,
  `creation_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`documentId`),
  UNIQUE KEY `document` (`documentId`),
  KEY `author` (`authorId`),
  KEY `date` (`dateindex`),
  KEY `datetime` (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `crawl_result`
--

DROP TABLE IF EXISTS `crawl_result`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `crawl_result` (
  `id` int(11) NOT NULL auto_increment,
  `blog_url` varchar(255) NOT NULL,
  `rss_url` varchar(255) default NULL,
  `total_crawl_count` int(11) NOT NULL default '0',
  `latest_blog_date` datetime default NULL,
  `feed_count` int(11) default NULL,
  `insert_count` int(11) default NULL,
  `status` enum('success','rss url not found','feed failed','rss url failed','page not found') NOT NULL,
  `crawl_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `index_blog_url` (`blog_url`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `rss_url`
--

DROP TABLE IF EXISTS `rss_url`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rss_url` (
  `id` int(11) NOT NULL auto_increment,
  `blog_url` varchar(255) NOT NULL,
  `rss_url` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `index_blog_url` (`blog_url`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `fx_rate`
--

DROP TABLE IF EXISTS `fx_rate`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fx_rate` (
  `currency` enum('USDJPY','EURJPY') NOT NULL,
  `date` date NOT NULL,
  `open` double NOT NULL,
  `high` double NOT NULL,
  `low` double NOT NULL,
  `close` double NOT NULL,
  PRIMARY KEY  (`currency`,`date`),
  KEY `index_currency` (`currency`),
  KEY `index_date` (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-11-13  4:17:23

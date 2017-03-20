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
-- Dumping data for table `prediction_columns`
--

LOCK TABLES `prediction_columns` WRITE;
/*!40000 ALTER TABLE `prediction_columns` DISABLE KEYS */;
INSERT INTO `prediction_columns` VALUES ('buzz','visible','クチコミ指数','65','35'),('pricenum','visible','価格指数','55','45'),('volume','visible','出来高指数','53','47'),('buzznew','hidden',NULL,NULL,NULL),('buzzrss','hidden',NULL,NULL,NULL),('rsi','hidden',NULL,NULL,NULL),('pricenumtl','hidden',NULL,NULL,NULL),('volumetl','hidden',NULL,NULL,NULL),('volumerv','hidden',NULL,NULL,NULL),('repute','hidden',NULL,NULL,NULL),('simvoted','hidden',NULL,NULL,NULL),('correct','visible',NULL,NULL,NULL),('voted','hidden',NULL,NULL,NULL),('voted1','visible','総合指数','80','20'),('voted2','hidden','総合指数','80','20'),('voted3','hidden','総合指数','80','20'),('voted4','hidden','総合指数','80','20'),('voted5','hidden','総合指数','80','20'),('losscut','visible','許容偏差',NULL,NULL),('notice','visible',NULL,NULL,NULL);
/*!40000 ALTER TABLE `prediction_columns` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-11-02 11:58:58

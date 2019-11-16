# docker run -it mysql:5.7 mysql --host <MySQL_HOST> --user <USERNAME> -p<PASSWORD>

CREATE TABLE IF NOT EXISTS rule
(
    `id`         INT AUTO_INCREMENT PRIMARY KEY,
    `metricName` VARCHAR(255)                                                                                                   NOT NULL,
    `condition`  ENUM ('GreaterThanOrEqualToThreshold','GreaterThanThreshold','LessThanOrEqualToThreshold','LessThanThreshold') NOT NULL,
    `threshold`  DOUBLE
) ENGINE = INNODB;

INSERT INTO rule (`metricName`, `condition`, `threshold`)
VALUES ('foo', 'GreaterThanOrEqualToThreshold', 1.23),
       ('bar', 'LessThanThreshold', 12.3),
       ('baz', 'LessThanOrEqualToThreshold', -1.41);
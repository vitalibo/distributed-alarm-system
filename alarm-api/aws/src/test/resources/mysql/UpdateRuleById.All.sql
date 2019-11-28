UPDATE `rule`
SET
    `metricName`='foo'
    ,
    `condition`='LessThanOrEqualToThreshold'
    ,
    `threshold`=1.23
WHERE `id` = bar
SELECT `metricName`, `condition`, `threshold`
FROM rule
WHERE `id` = ${ruleId}
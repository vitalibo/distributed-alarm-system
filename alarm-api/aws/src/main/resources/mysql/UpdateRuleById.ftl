UPDATE `rule`
SET
<#if rule.metricName?has_content>
    `metricName`='${rule.metricName}'
</#if>
<#if rule.condition?has_content>
    <#if rule.metricName?has_content>,</#if>
    `condition`='${rule.condition}'
</#if>
<#if rule.threshold?has_content>
    <#if rule.metricName?has_content || rule.condition?has_content>,</#if>
    `threshold`=${rule.threshold}
</#if>
WHERE `id` = ${ruleId}
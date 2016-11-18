<#-- Patch pour MNT-15977 Create document (folder) from template' does not sort nor is scrollable -->
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   [
   <#list nodes?sort_by(["properties","cm:name"]) as node>
      {
         "nodeRef": "${node.nodeRef}",
         "name": "${node.name}",
         "title": "${node.properties.title!""}",
         "description": "${node.properties.description!""}"
      }<#if node_has_next>,</#if>
   </#list>
   ]
}
</#escape>
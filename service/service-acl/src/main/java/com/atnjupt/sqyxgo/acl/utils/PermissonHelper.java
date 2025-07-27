package com.atnjupt.sqyxgo.acl.utils;

import com.atnjupt.sqyxgo.model.acl.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:PermissonHelper
 * Package: com.atnjupt.sqyxgo.acl.utils
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 8:48
 * @Version 1.0
 */
public class PermissonHelper {

    public static List<Permission> buildPermisson(List<Permission> treeNodes){
        List<Permission> trees = new ArrayList<>();
        for (Permission permission : treeNodes) {
            if (permission.getPid() == 0){
                permission.setLevel(1);
                trees.add(findChildren(permission,treeNodes));
            }

        }
        return trees;
    }

    public static Permission findChildren(Permission treeNode,List<Permission> treeNodes){
        treeNode.setChildren(new ArrayList<Permission>());

        for (Permission node : treeNodes) {
            if (node.getPid().equals(treeNode.getId())) {
                node.setLevel(treeNode.getLevel() + 1);
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<Permission>());
                }
                treeNode.getChildren().add(findChildren(node,treeNodes));
            }

        }

        return treeNode;

    }

}

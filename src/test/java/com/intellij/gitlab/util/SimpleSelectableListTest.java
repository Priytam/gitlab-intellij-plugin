package com.intellij.gitlab.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

public class SimpleSelectableListTest {

    @Test
    public void test_add(){
        SimpleSelectableList<String> myList = SimpleSelectableList.empty();

        myList.add("Bananas");
        Assert.assertEquals("Bananas", myList.getSelectedItem());

        myList.add("Apples");
        Assert.assertEquals("Bananas", myList.getSelectedItem());

        myList.add("Oranges", true);
        Assert.assertEquals("Oranges", myList.getSelectedItem());
    }


    @Test
    public void test_update_one(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 1);

        myList.update(1, "Apples2", false);

        Assert.assertEquals("Bananas", myList.getSelectedItem());
        Assert.assertEquals("Apples2", myList.getItems().get(1));
    }

    @Test
    public void test_update_two(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 1);

        myList.update(2, "Oranges", true);
        Assert.assertEquals("Oranges", myList.getSelectedItem());
    }

    @Test
    public void test_remove_case_one(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 2);

        myList.remove(2);
        Assert.assertEquals("Apples", myList.getSelectedItem());
    }

    @Test
    public void test_remove_case_two(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 1);

        myList.remove(1);
        Assert.assertEquals("Oranges", myList.getSelectedItem());
    }

    @Test
    public void test_remove_case_three(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 1);

        myList.remove(0);
        Assert.assertEquals("Apples", myList.getSelectedItem());
    }

    @Test
    public void test_clear(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"), 1);

        myList.clear();
        Assert.assertEquals(-1, myList.getSelectedItemIndex());
    }

    @Test
    public void test_select_item(){
        SimpleSelectableList<String> myList = SimpleSelectableList.of(Lists.newArrayList("Bananas", "Apples", "Oranges"));

        myList.selectItem(4);
        Assert.assertEquals("Oranges", myList.getSelectedItem());
    }


}
package com.intellij.gitlab.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleSelectableList<E>{
    private static final int UNSELECTED = -1;
    private static final Integer MIN_SELECTED = 0;
    private static final Integer MAX_SELECTED = Integer.MAX_VALUE;

    private List<E> items;
    private int selectedItem;

    public SimpleSelectableList() {
        this(new ArrayList<>(), UNSELECTED);
    }

    private SimpleSelectableList(List<E> items){
        this(items, MIN_SELECTED);
    }

    private SimpleSelectableList(List<E> items, int selectedItem){
        this.items = items;
        selectItem(selectedItem);
    }

    public static <E> SimpleSelectableList<E> empty(){
        return new SimpleSelectableList<>();
    }

    public static <E> SimpleSelectableList<E> of(List<E> items){
        return new SimpleSelectableList<>(items);
    }

    public static <E> SimpleSelectableList<E> of(List<E> items, int selectedItem){
        return new SimpleSelectableList<>(items, selectedItem);
    }

    public boolean add(E item){
        if(items.isEmpty()){
            items.add(item);
            selectItem(MIN_SELECTED);
        }else{
            items.add(item);
        }

        return true;
    }


    public void add(E item, boolean selected){
        add(item);
        if(selected){
            selectItem(getLastSelectableItemIndex());
        }
    }


    public boolean addAll(Collection<? extends E> items) {
        this.items.addAll(items);
        if(!hasSelectedItem() && !items.isEmpty()){
            selectItem(MIN_SELECTED);
        }

        return true;
    }

    public void update(int index, E item, boolean selected){
        if(index >= MIN_SELECTED && index <= getLastSelectableItemIndex()){
            this.items.remove(index);
            this.items.add(index, item);
            updateSelectedItem(index, selected);
        }

    }

    public E remove(E item){
        return remove(items.indexOf(item));
    }

    public E remove(int index){
        if(index < 0 || index > getLastSelectableItemIndex()){
            return null;
        }

        E element = this.items.remove(index);
        if(this.selectedItem > getLastSelectableItemIndex()){
            selectItem(getSelectedItemIndex() - 1);
        }

        if(index < this.selectedItem){
            selectItem(index);
        }

        return element;
    }


    public void clear() {
        items.clear();
        this.selectedItem = UNSELECTED;
    }


    public List<E> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getSelectedItemIndex() {
        return selectedItem;
    }

    @Nullable
    public E getSelectedItem(){
        if(hasSelectedItem()){
            return this.items.get(getSelectedItemIndex());
        }

        return null;
    }


    public void updateSelectedItem(E item, boolean selected){
        updateSelectedItem(items.indexOf(item), selected);
    }

    private void updateSelectedItem(int index, boolean selected){
        if(selected){
            selectItem(index);
        }else{
            unselectItem(index);
        }
    }

    public void selectItem(E item){
        selectItem(this.items.indexOf(item));
    }

    public void selectItem(int selectedItem) {
        if(selectedItem < MIN_SELECTED ){
            this.selectedItem = UNSELECTED;
        }else if(selectedItem > getLastSelectableItemIndex()){
            this.selectedItem = getLastSelectableItemIndex();
        }else{
            this.selectedItem = selectedItem;
        }
    }

    public boolean isEmpty(){
        return this.items.isEmpty();
    }

    public boolean hasSelectedItem(){
        return selectedItem > UNSELECTED && selectedItem < this.items.size();
    }

    private int getLastSelectableItemIndex(){
        return isEmpty() ? UNSELECTED : items.size() - 1;
    }

    private void unselectItem(int index){
        if(index == selectedItem){
            selectItem(items.size() == 1 ? MIN_SELECTED : (index - 1));
        }
    }

}

package com.cirsim.expressions;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class NodeImpl implements Node {


    protected final List<Node> children = Lists.newArrayList();

    protected Node parent;
    protected ListIterator<Node> iterator = children.listIterator();
    protected Node lastChild;


    @Override
    public double getChildValue( int _index ) {

        if( _index >= children.size() )
            throw new IndexOutOfBoundsException( "Number of children: " + children.size() + "; index: " + _index );

        return children.get( _index ).getValue();
    }


    @Override
    public Node getParent() {
        return parent;
    }


    @Override
    public void setParent( final Node _parent ) {
        parent = _parent;
    }


    @Override
    public boolean hasParent() {
        return parent != null;
    }


    @Override
    public void rewind() {
        iterator = children.listIterator();
    }


    @Override
    public boolean hasNextChild() {
        return iterator.hasNext();
    }


    @Override
    public void addChild( final Node _child ) {
        children.add( _child );
    }


    @Override
    public Node nextChild() {
        lastChild = iterator.next();
        return lastChild;
    }


    @Override
    public Node replaceLastChild( final Node _child ) {
        iterator.set( _child );
        return lastChild;
    }
}

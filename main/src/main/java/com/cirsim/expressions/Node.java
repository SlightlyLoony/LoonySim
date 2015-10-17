package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Node {

    Node getParent();

    void setParent( Node _parent );

    boolean hasParent();

    void rewind();

    boolean hasNextChild();

    void addChild( Node _child );

    Node nextChild();

    Node replaceLastChild( Node _child );

    double getValue();

    double getChildValue( int _index );
}

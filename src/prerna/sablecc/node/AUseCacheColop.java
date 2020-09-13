/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.Analysis;

@SuppressWarnings("nls")
public final class AUseCacheColop extends PColop
{
    private PUseCache _useCache_;

    public AUseCacheColop()
    {
        // Constructor
    }

    public AUseCacheColop(
        @SuppressWarnings("hiding") PUseCache _useCache_)
    {
        // Constructor
        setUseCache(_useCache_);

    }

    @Override
    public Object clone()
    {
        return new AUseCacheColop(
            cloneNode(this._useCache_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUseCacheColop(this);
    }

    public PUseCache getUseCache()
    {
        return this._useCache_;
    }

    public void setUseCache(PUseCache node)
    {
        if(this._useCache_ != null)
        {
            this._useCache_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._useCache_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._useCache_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._useCache_ == child)
        {
            this._useCache_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._useCache_ == oldChild)
        {
            setUseCache((PUseCache) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

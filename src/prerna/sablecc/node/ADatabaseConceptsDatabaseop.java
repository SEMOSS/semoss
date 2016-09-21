/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ADatabaseConceptsDatabaseop extends PDatabaseop
{
    private PDatabaseConcepts _databaseConcepts_;

    public ADatabaseConceptsDatabaseop()
    {
        // Constructor
    }

    public ADatabaseConceptsDatabaseop(
        @SuppressWarnings("hiding") PDatabaseConcepts _databaseConcepts_)
    {
        // Constructor
        setDatabaseConcepts(_databaseConcepts_);

    }

    @Override
    public Object clone()
    {
        return new ADatabaseConceptsDatabaseop(
            cloneNode(this._databaseConcepts_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADatabaseConceptsDatabaseop(this);
    }

    public PDatabaseConcepts getDatabaseConcepts()
    {
        return this._databaseConcepts_;
    }

    public void setDatabaseConcepts(PDatabaseConcepts node)
    {
        if(this._databaseConcepts_ != null)
        {
            this._databaseConcepts_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._databaseConcepts_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._databaseConcepts_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._databaseConcepts_ == child)
        {
            this._databaseConcepts_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._databaseConcepts_ == oldChild)
        {
            setDatabaseConcepts((PDatabaseConcepts) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

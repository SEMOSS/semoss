/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class APanelLookAndFeelPanelop extends PPanelop
{
    private PPanelLookAndFeel _panelLookAndFeel_;

    public APanelLookAndFeelPanelop()
    {
        // Constructor
    }

    public APanelLookAndFeelPanelop(
        @SuppressWarnings("hiding") PPanelLookAndFeel _panelLookAndFeel_)
    {
        // Constructor
        setPanelLookAndFeel(_panelLookAndFeel_);

    }

    @Override
    public Object clone()
    {
        return new APanelLookAndFeelPanelop(
            cloneNode(this._panelLookAndFeel_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPanelLookAndFeelPanelop(this);
    }

    public PPanelLookAndFeel getPanelLookAndFeel()
    {
        return this._panelLookAndFeel_;
    }

    public void setPanelLookAndFeel(PPanelLookAndFeel node)
    {
        if(this._panelLookAndFeel_ != null)
        {
            this._panelLookAndFeel_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._panelLookAndFeel_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._panelLookAndFeel_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._panelLookAndFeel_ == child)
        {
            this._panelLookAndFeel_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._panelLookAndFeel_ == oldChild)
        {
            setPanelLookAndFeel((PPanelLookAndFeel) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

package org.fit.vips;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.layout.TextBox;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VipsBlock {

	private Box _box = null;
	private List<VipsBlock> _children = null;
	private int _id = 0;
	private int _DoC = 0;

	private int _containImg = 0;
	private boolean _isImg = false;
	private boolean _isVisualBlock = false;
	private boolean _containTable = false;
	private int _containP = 0;
	private boolean _alreadyDivided = false;
	private boolean _isDividable = true;

	private String _bgColor = null;

	private int _frameSourceIndex = 0;
	private int _sourceIndex = 0;
	private int _tmpSrcIndex = 0;
	private int _order = 0;

	private int _textLen = 0;
	private int _linkTextLen = 0;

	public VipsBlock() {
		this._children = new ArrayList<VipsBlock>();
	}

	public VipsBlock(int id, VipsBlock node) {
		this._children = new ArrayList<VipsBlock>();
		setId(id);
		addChild(node);
	}

	public void setIsVisualBlock(boolean isVisualBlock)
	{
		_isVisualBlock = isVisualBlock;
		checkProperties();
	}

	public boolean isVisualBlock()
	{
		return _isVisualBlock;
	}

	private void checkProperties()
	{
		checkIsImg();
		checkContainImg(this);
		checkContainTable(this);
		checkContainP(this);
		_linkTextLen = 0;
		_textLen = 0;
		countTextLength(this);
		countLinkTextLength(this);
		setSourceIndex(this.getBox().getNode().getOwnerDocument());
	}

	private void checkIsImg()
	{
		if (_box.getNode().getNodeName().equals("img"))
			_isImg = true;
		else
			_isImg = false;
	}

	private void checkContainImg(VipsBlock vipsBlock)
	{
		if (vipsBlock.getBox().getNode().getNodeName().equals("img"))
		{
			vipsBlock._isImg = true;
			this._containImg++;
		}

		for (VipsBlock childVipsBlock : vipsBlock.getChildren())
			checkContainImg(childVipsBlock);
	}

	private void checkContainTable(VipsBlock vipsBlock)
	{
		if (vipsBlock.getBox().getNode().getNodeName().equals("table"))
			this._containTable = true;

		for (VipsBlock childVipsBlock : vipsBlock.getChildren())
			checkContainTable(childVipsBlock);
	}

	private void checkContainP(VipsBlock vipsBlock)
	{
		if (vipsBlock.getBox().getNode().getNodeName().equals("p"))
			this._containP++;

		for (VipsBlock childVipsBlock : vipsBlock.getChildren())
			checkContainP(childVipsBlock);
	}

	private void countLinkTextLength(VipsBlock vipsBlock)
	{
		if (vipsBlock.getBox().getNode().getNodeName().equals("a"))
		{
			_linkTextLen += vipsBlock.getBox().getText().length();

		}

		for (VipsBlock childVipsBlock : vipsBlock.getChildren())
			countLinkTextLength(childVipsBlock);
	}

	private void countTextLength(VipsBlock vipsBlock)
	{
		_textLen = vipsBlock.getBox().getText().replaceAll("\n", "").length();
	}

	public void addChild(VipsBlock child)
	{
		_children.add(child);
	}

	public List<VipsBlock> getChildren()
	{
		return _children;
	}

	public void setBox(Box box)
	{
		this._box = box;
	}

	public Box getBox()
	{
		return _box;
	}

	public ElementBox getElementBox()
	{
		if (_box instanceof ElementBox)
			return (ElementBox) _box;
		else
			return null;
	}

	public void setId(int id)
	{
		this._id = id;
	}

	public int getId()
	{
		return _id;
	}

	public int getDoC()
	{
		return _DoC;
	}

	public void setDoC(int doC)
	{
		this._DoC = doC;
	}

	public boolean isDividable()
	{
		return _isDividable;
	}

	public void setIsDividable(boolean isDividable)
	{
		this._isDividable = isDividable;
	}

	public boolean isAlreadyDivided()
	{
		return _alreadyDivided;
	}

	public void setAlreadyDivided(boolean alreadyDivided)
	{
		this._alreadyDivided = alreadyDivided;
	}

	public boolean isImg()
	{
		return _isImg;
	}

	public int containImg()
	{
		return _containImg;
	}

	public boolean containTable()
	{
		return _containTable;
	}

	public int getTextLength()
	{
		return _textLen;
	}

	public int getLinkTextLength()
	{
		return _linkTextLen;
	}

	public int containP()
	{
		return _containP;
	}

	private void findBgColor(Element element)
	{
		String backgroundColor = element.getAttribute("background-color");

		if (backgroundColor.isEmpty())
		{
			if (element.getParentNode() != null &&
			    !element.getTagName().equals("body"))
			{
				findBgColor((Element) element.getParentNode());
			}
			else
			{
				_bgColor = "#ffffff";
				return;
			}
		}
		else
		{
			_bgColor = backgroundColor;
			return;
		}
	}

	public String getBgColor()
	{
		if (_bgColor != null)
			return _bgColor;

		if (this.getBox() instanceof TextBox)
		{
			_bgColor = "#ffffff";
		}
		else
		{
			_bgColor = this.getElementBox().getStylePropertyValue("background-color");
		}


		if (_bgColor.isEmpty())
			findBgColor(this.getElementBox().getElement());

		return _bgColor;
	}

	public int getFontSize()
	{
		return this.getBox().getVisualContext().getFont().getSize();
	}

	public String getFontWeight()
	{
		String fontWeight = "";

		if (this.getBox() instanceof TextBox)
		{
			return fontWeight;
		}

		if (this.getElementBox().getStylePropertyValue("font-weight") == null)
			return fontWeight;

		fontWeight = this.getElementBox().getStylePropertyValue("font-weight");

		if (fontWeight.isEmpty())
			fontWeight = "normal";

		return fontWeight;
	}

	public int getFrameSourceIndex()
	{
		return _frameSourceIndex;
	}

	private void setSourceIndex(Node node)
	{
		if (!this.getBox().getNode().equals(node))
			_tmpSrcIndex++;
		else
			_sourceIndex = _tmpSrcIndex;

		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			setSourceIndex(node.getChildNodes().item(i));
		}
	}

	public int getSourceIndex()
	{
		return _sourceIndex;
	}

	public int getOrder()
	{
		return _order;
	}

}

package org.fit.vips;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class VisualStructure {

	private List<VipsBlock> _nestedBlocks = null;
	private List<VisualStructure> _childrenVisualStructures = null;
	private List<Separator> _horizontalSeparators = null;
	private List<Separator> _verticalSeparators = null;
	private int _width = 0;
	private int _height = 0;
	private int _x = 0;
	private int _y = 0;
	private int _doC = 12;
	private int _containImg = -1;
	private int _containP = -1;
	private int _textLength = -1;
	private int _linkTextLength = -1;
	private int _order;
	private boolean _containTable = false;
	private String _id = null;
	private int _tmpSrcIndex = 0;
	private int _srcIndex = 0;
	private int _minimalDoC = 0;

	public VisualStructure()
	{
		_nestedBlocks = new ArrayList<VipsBlock>();
		_childrenVisualStructures = new ArrayList<VisualStructure>();
		_horizontalSeparators = new ArrayList<Separator>();
		_verticalSeparators = new ArrayList<Separator>();
	}


	public List<VipsBlock> getNestedBlocks()
	{
		return _nestedBlocks;
	}

	public void addNestedBlock(VipsBlock nestedBlock)
	{
		this._nestedBlocks.add(nestedBlock);
	}

	public void addNestedBlocks(List<VipsBlock> nestedBlocks)
	{
		this._nestedBlocks.addAll(nestedBlocks);
	}

	public void setNestedBlocks(List<VipsBlock> vipsBlocks)
	{
		this._nestedBlocks = vipsBlocks;
	}

	public void clearNestedBlocks()
	{
		this._nestedBlocks.clear();
	}

	public void removeNestedBlockAt(int index)
	{
		this._nestedBlocks.remove(index);
	}

	public void removeChild(VisualStructure visualStructure)
	{
		this._childrenVisualStructures.remove(visualStructure);
	}

	public void addChild(VisualStructure visualStructure)
	{
		this._childrenVisualStructures.add(visualStructure);
	}

	public void addChildAt(VisualStructure visualStructure, int index)
	{
		this._childrenVisualStructures.add(index, visualStructure);
	}

	public List<VisualStructure> getChildrenVisualStructures()
	{
		return _childrenVisualStructures;
	}

	public void setChildrenVisualStructures(List<VisualStructure> childrenVisualStructures)
	{
		this._childrenVisualStructures = childrenVisualStructures;
	}

	public List<Separator> getHorizontalSeparators()
	{
		return _horizontalSeparators;
	}

	public void setHorizontalSeparators(List<Separator> horizontalSeparators)
	{
		this._horizontalSeparators = horizontalSeparators;
	}

	public void addHorizontalSeparator(Separator horizontalSeparator)
	{
		this._horizontalSeparators.add(horizontalSeparator);
	}

	public void addHorizontalSeparators(List<Separator> horizontalSeparators)
	{
		this._horizontalSeparators.addAll(horizontalSeparators);
	}

	public int getX()
	{
		return this._x;
	}

	public int getY()
	{
		return this._y;
	}

	public void setX(int x)
	{
		this._x = x;
	}

	public void setY(int y)
	{
		this._y = y;
	}

	
	public void setWidth(int width)
	{
		this._width = width;
	}

	
	public void setHeight(int height)
	{
		this._height = height;
	}

	
	public int getWidth()
	{
		return this._width;
	}

	
	public int getHeight()
	{
		return this._height;
	}

	
	public List<Separator> getVerticalSeparators()
	{
		return _verticalSeparators;
	}

	
	public void setVerticalSeparators(List<Separator> _verticalSeparators)
	{
		this._verticalSeparators = _verticalSeparators;
	}

	
	public void addVerticalSeparator(Separator verticalSeparator)
	{
		this._verticalSeparators.add(verticalSeparator);
	}

	
	public void setId(String id)
	{
		this._id = id;
	}

	
	public String getId()
	{
		return this._id;
	}

	
	public void setDoC(int doC)
	{
		this._doC = doC;
	}

	
	public int getDoC()
	{
		return _doC;
	}

	
	private void findMinimalDoC(VisualStructure visualStructure)
	{
		if (!visualStructure.getId().equals("1"))
		{
			if (visualStructure.getDoC() < _minimalDoC)
				_minimalDoC = visualStructure.getDoC();
		}

		for (VisualStructure child : visualStructure.getChildrenVisualStructures())
		{
			findMinimalDoC(child);
		}
	}

	public void updateToNormalizedDoC()
	{
		_doC = 12;

		for (Separator separator : _horizontalSeparators)
		{
			if (separator.normalizedWeight < _doC)
				_doC = separator.normalizedWeight;
		}

		for (Separator separator : _verticalSeparators)
		{
			if (separator.normalizedWeight < _doC)
				_doC = separator.normalizedWeight;
		}

		if (_doC == 12)
		{
			for (VipsBlock nestedBlock : _nestedBlocks)
			{
				if (nestedBlock.getDoC() < _doC)
					_doC = nestedBlock.getDoC();
			}
		}

		_minimalDoC = 12;

		findMinimalDoC(this);

		if (_minimalDoC < _doC)
			_doC = _minimalDoC;
	}

	
	public int containImg()
	{
		if (_containImg != -1)
			return _containImg;

		_containImg = 0;

		for (VipsBlock vipsBlock : _nestedBlocks)
		{
			_containImg += vipsBlock.containImg();
		}

		return _containImg;
	}

	
	public int containP()
	{
		if (_containP != -1)
			return _containP;

		_containP = 0;

		for (VipsBlock vipsBlock : _nestedBlocks)
		{
			_containP += vipsBlock.containP();
		}

		return _containP;
	}

	
	public boolean containTable()
	{
		if (_containTable)
			return _containTable;

		for (VipsBlock vipsBlock : _nestedBlocks)
		{
			if (vipsBlock.containTable())
			{
				_containTable = true;
				break;
			}
		}

		return _containTable;
	}

	
	public boolean isImg()
	{
		if (_nestedBlocks.size() != 1)
			return false;

		return _nestedBlocks.get(0).isImg();
	}

	
	public int getTextLength()
	{
		if (_textLength != -1)
			return _textLength;

		_textLength = 0;
		for (VipsBlock vipsBlock : _nestedBlocks)
		{
			_textLength += vipsBlock.getTextLength();
		}

		return _textLength;
	}

	
	public int getLinkTextLength()
	{
		if (_linkTextLength != -1)
			return _linkTextLength;

		_linkTextLength = 0;
		for (VipsBlock vipsBlock : _nestedBlocks)
		{
			_linkTextLength += vipsBlock.getLinkTextLength();
		}

		return _linkTextLength;
	}

	
	public int getFontSize()
	{
		if (_nestedBlocks.size() > 0)
			return _nestedBlocks.get(0).getFontSize();
		else
			return -1;
	}

	
	public String getFontWeight()
	{
		if (_nestedBlocks.size() > 0)
			return _nestedBlocks.get(0).getFontWeight();
		else
			return "undef";
	}

	
	public String getBgColor()
	{
		if (_nestedBlocks.size() > 0)
			return _nestedBlocks.get(0).getBgColor();
		else
			return "undef";
	}

	
	public int getFrameSourceIndex()
	{
		if (_nestedBlocks.size() > 0)
			return _nestedBlocks.get(0).getFrameSourceIndex();
		else
			return -1;
	}


	private void setSourceIndex(Node node, Node nodeToFind)
	{
		if (!nodeToFind.equals(node))
			_tmpSrcIndex++;
		else
			_srcIndex = _tmpSrcIndex;

		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			setSourceIndex(node.getChildNodes().item(i), nodeToFind);
		}
	}

	
	public String getSourceIndex()
	{
		String sourceIndex = "";

		if (_childrenVisualStructures.size() > 0)
		{
			setSourceIndex(_nestedBlocks.get(0).getBox().getNode().getOwnerDocument(), _nestedBlocks.get(0).getBox().getParent().getNode());
			sourceIndex = String.valueOf(_srcIndex);
		}
		else
		{
			for (VipsBlock block : _nestedBlocks)
			{
				if (!sourceIndex.equals(""))
					sourceIndex += ";";

				sourceIndex += block.getSourceIndex();
			}
		}
		return sourceIndex;
	}

	
	public void setOrder(int order)
	{
		this._order = order;
	}

	
	public int getOrder()
	{
		return _order;
	}

	
	public void addVerticalSeparators(List<Separator> verticalSeparators)
	{
		this._verticalSeparators.addAll(verticalSeparators);
	}
}

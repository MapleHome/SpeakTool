package com.speektool.impl.modes;

/**
 * 绘画模式——橡皮擦
 * 
 * @author shaoshuai
 * 
 */
public class DrawModeEraser extends DrawModePath {
	
	public DrawModeEraser() {
		super();
	}

	@Override
	public DrawModeCode getModeCode() {
		return DrawModeCode.ERASER;
	}
}

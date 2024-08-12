package com.freedarwuin.pvptools;

import java.awt.Color;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableElement
{
	TableAlignment alignment;
	Color color;
	String content;
}
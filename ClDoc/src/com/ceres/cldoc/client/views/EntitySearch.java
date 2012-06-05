package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ClickableTree;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class EntitySearch extends SplitLayoutPanel {

	public EntitySearch(final ClDoc clDoc) {
		ClickableTree<EntityRelation> tree = new ClickableTree<EntityRelation>(clDoc, true) {};
		ClickableTable<Entity> table = new ClickableTable<Entity>(clDoc) {

			@Override
			public void addRow(FlexTable table, int row, Entity entry) {
				table.setWidget(row, 0, new Label(entry.name));
			}
		};
		addWest(tree, 300);
		add(table);
	}

}

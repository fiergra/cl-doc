package eu.europa.ec.digit.client;

import java.util.Collection;

import com.ceres.dynamicforms.client.components.LabelFunc;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class MultiSelectPanel<T> extends HorizontalPanel {

	private LabelFunc<T> labelFunc;

	public MultiSelectPanel(Collection<T> owners, LabelFunc<T> labelFunc) {
		setStyleName("multiSelectPanel");
		setSpacing(3);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.labelFunc = labelFunc;
		refresh(owners);
	}

	protected boolean onAdd() {
		return false;
	}

	protected boolean onDelete(T t) {
		return false;
	}

	private Widget createRenderer(T t, LabelFunc<T> labelFunc) {
		
		HorizontalPanel hpRenderer = new HorizontalPanel();

		Image imgDelete = new Image("assets/images/delete.png");
		PushButton pbDelete = new PushButton(imgDelete);
		pbDelete.addClickHandler(e -> { 
			if (onDelete(t)) {
				remove(hpRenderer);
			}
		});
		pbDelete.setStyleName("blankButton");
		imgDelete.setPixelSize(14, 14);
		pbDelete.setPixelSize(16, 16);

		hpRenderer.add(pbDelete);
		hpRenderer.add(new Label(labelFunc.label(t)));
		
		return hpRenderer;
	}

	public void refresh(Collection<T> entries) {
		clear();
		if (entries != null) {
			entries.forEach(t -> add(createRenderer(t, labelFunc)));
		}
		Image imgAdd = new Image("assets/images/add.png");
		PushButton pbAdd = new PushButton(imgAdd);
		pbAdd.addClickHandler(e -> onAdd());
		pbAdd.setStyleName("blankButton");
		imgAdd.setPixelSize(14, 14);
		pbAdd.setPixelSize(16, 16);
		add(pbAdd); 
	}
	
}

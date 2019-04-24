package eu.europa.ec.digit.client;

import java.util.Collection;

import com.ceres.dynamicforms.client.components.EnabledHorizontalPanel;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class MultiSelectPanel<T> extends EnabledHorizontalPanel {

	private LabelFunc<T> labelFunc;
	private LabelFunc<T> toolTipFunc;

	public MultiSelectPanel(Collection<T> elements, LabelFunc<T> labelFunc) {
		this(elements, labelFunc, null);
	}
	
	public MultiSelectPanel(Collection<T> elements, LabelFunc<T> labelFunc, LabelFunc<T> toolTipFunc) {
		setStyleName("multiSelectPanel");
		setSpacing(3);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.labelFunc = labelFunc;
		this.toolTipFunc = toolTipFunc;
		refresh(elements);
	}

	protected boolean onAdd() {
		return false;
	}

	protected boolean onDelete(T t) {
		return false;
	}

	private Widget createRenderer(T t, LabelFunc<T> labelFunc) {
		
		HorizontalPanel hpRenderer = new HorizontalPanel();

		Image imgDelete = new Image("assets/images/16x16/minus.png");
		PushButton pbDelete = new PushButton(imgDelete);
		pbDelete.addClickHandler(e -> {
			if (isEnabled() && onDelete(t)) {
				remove(hpRenderer);
			}
		});
		pbDelete.setStyleName("mspButton");
		imgDelete.setPixelSize(14, 14);
		pbDelete.setPixelSize(16, 16);

		hpRenderer.add(pbDelete);
		Label label = new Label(labelFunc.label(t));
		if (toolTipFunc != null) {
			label.setTitle(toolTipFunc.label(t));
		}
		hpRenderer.add(label);
		
		return hpRenderer;
	}

	public void refresh(Collection<T> entries) {
		clear();
		if (entries != null) {
			entries.forEach(t -> add(createRenderer(t, labelFunc)));
		}
		Image imgAdd = new Image("assets/images/16x16/add.png");
		PushButton pbAdd = new PushButton(imgAdd);
		pbAdd.addClickHandler(e -> onAdd());
		pbAdd.setStyleName("mspButton");
		imgAdd.setPixelSize(14, 14);
		pbAdd.setPixelSize(16, 16);
		add(pbAdd); 
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (isEnabled() != enabled) {
			if (enabled) {
				removeStyleName("disabled");
			} else {
				addStyleName("disabled");
			}
		}
		super.setEnabled(enabled);
	}
	
	
	
}

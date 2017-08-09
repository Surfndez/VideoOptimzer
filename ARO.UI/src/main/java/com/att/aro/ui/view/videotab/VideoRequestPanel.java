package com.att.aro.ui.view.videotab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.att.aro.core.packetanalysis.pojo.HttpRequestResponseInfo;
import com.att.aro.core.pojo.AROTraceData;
import com.att.aro.ui.commonui.TabPanelJScrollPane;
import com.att.aro.ui.view.menu.tools.RegexWizard;

public class VideoRequestPanel extends TabPanelJScrollPane {

	private static final long serialVersionUID = 1L;

	private JPanel requestListPanel;

	private List<HttpRequestResponseInfo> requestURL = new ArrayList<>();

	private JPanel requestPanel;

	private JTable requestListTable;
	
	public VideoRequestPanel() {

		requestPanel = new JPanel();
		requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.PAGE_AXIS));
		
		requestPanel.setBackground(new Color(238, 238, 238));
		requestPanel.add(getRequestListPanel());
		
		setViewportView(requestPanel);
	}

	private JPanel getRequestListPanel() {
		if (requestListPanel == null) {
			requestListPanel = new JPanel();
			requestListPanel.setLayout(new BorderLayout());
			getDummyData();
			TableModel model = new AbstractTableModel() {
				String[] columnNames = { "Request URL" };

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					Object value = "";
					if (columnIndex == 0) {
						if (requestURL.get(rowIndex) != null) {
							value = requestURL.get(rowIndex).getObjUri().toString();
						} else {
							value = "";
						}
					}
					return value;
				}

				@Override
				public int getRowCount() {
					return requestURL.size();
				}

				@Override
				public int getColumnCount() {
					return columnNames.length;
				}

				@Override
				public String getColumnName(int columnIndex) {
					return columnNames[columnIndex];
				}
			};

			requestListTable = new JTable(model);
			JTableHeader header = requestListTable.getTableHeader();
			requestListTable.setGridColor(Color.LIGHT_GRAY);
			int width = requestListTable.getParent() != null ? requestListTable.getParent().getWidth() : 1000;
			width = requestPanel.getWidth();
			requestListTable.getColumnModel().getColumn(0).setPreferredWidth(width);
			requestListTable.getColumnModel().getColumn(0).setCellRenderer(new WordWrapRenderer());

			requestListPanel.add(header, BorderLayout.NORTH);
			requestListPanel.add(requestListTable, BorderLayout.CENTER);

			requestListTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					int row = requestListTable.getSelectedRow();
					HttpRequestResponseInfo request = requestURL.get(row);
					if (event.getClickCount() == 2) {
						requestListTable.getColumnModel().getColumn(0).setCellRenderer(new WordWrapRenderer(row));
						RegexWizard regexWizard = RegexWizard.getInstance();
						regexWizard.setRequest(request);
						regexWizard.setVisible(true);
						regexWizard.setAlwaysOnTop(true);
					}
				}
			});

		}
		return requestListPanel;
	}

	private void getDummyData() {
		requestURL = new ArrayList<>();
		// fill out a big empty
		for (int idx = 0; idx < 30; idx++) {
			requestURL.add(null);
		}
	}

	public void resize(){
		int width = requestPanel.getWidth() - 10;
		requestListTable.getColumnModel().getColumn(0).setPreferredWidth(width);
	}
	
	@Override
	public JPanel layoutDataPanel() {
		return null;
	}

	@Override
	public void refresh(AROTraceData analyzerResult) {
		requestURL = new ArrayList<>();
		if (analyzerResult != null && analyzerResult.getAnalyzerResult() != null && analyzerResult.getAnalyzerResult().getVideoUsage() != null) {
			for ( HttpRequestResponseInfo req : analyzerResult.getAnalyzerResult().getVideoUsage().getRequestMap().values()) {
				if (!req.getObjName().contains(".m3u8") && !req.getObjName().contains(".mpd")) {
					requestURL.add(req);
				}
			}
		} 
		requestPanel.remove(requestListPanel);
		requestPanel.add(getRequestListPanel(),
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

		requestPanel.updateUI();
	}

	@Override
	public void setScrollLocationMap() {	
	}


}
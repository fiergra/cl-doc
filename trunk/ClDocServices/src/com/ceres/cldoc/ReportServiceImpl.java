package com.ceres.cldoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.core.ISession;

public class ReportServiceImpl implements IReportService {

	@Override
	public List<ReportDefinition> list(ISession session, final Long type) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<ReportDefinition> execute(Connection con) throws SQLException {
				List<ReportDefinition> result = new ArrayList<ReportDefinition>();
				int i = 1;
				String sql = "select * from Report where 1=1 ";
				
				if (type != null) {
					sql += " AND type = ?";
				}
				PreparedStatement s = con.prepareStatement(sql);
				if (type != null) {
					s.setLong(i++, type);
				}
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					ReportDefinition rd = new ReportDefinition();
					rd.id = rs.getLong("id");
					rd.name = rs.getString("name");
					rd.type = rs.getLong("type");
					rd.xml = rs.getString("xml");

					result.add(rd);
				}
				rs.close();
				s.close();
				
				return result;
			}
		});
	}

	@Override
	public List<HashMap<String, Serializable>> execute(ISession session, final ReportDefinition rd, final IAct filters) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<HashMap<String, Serializable>> execute(Connection con) throws Exception {
				String query = getQuery(rd, filters);

				List<HashMap<String, Serializable>> result = new ArrayList<HashMap<String, Serializable>>();
				PreparedStatement s;
				
				if (rd.name.equals("Veranstaltungen")) {
					s = con.prepareStatement("select date(a.date) Datum, e.name Veranstalter, " +
							" (select stringValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Thema' and af.actId = a.id) Thema, " +
							" (select stringValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Ort/Lokal' and af.actId = a.id) Lokal " +
							" from Act a " +
							" inner join Participation p on p.actId = a.id and p.role = 101 " +
							" inner join Entity e on e.id = p.entityId " +
							" inner join Participation orgP on orgP.actId = a.id and orgP.role = 102 " +
							" inner join Entity orgE on orgE.id = orgP.entityId" +
							" inner join ActClass ac on a.ActClassId = ac.id" +
							" where ac.name = 'Veranstaltungsbericht'");
				} else if (rd.name.equals("Beratungen")) {
					s = con.prepareStatement("select e.name Klient, date(a.date) Datum, orgE.name orga, " +
							" (select stringValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Ort' and af.actId = a.id) Ort " +
							" from Act a inner join ActClass ac on a.ActClassId = ac.id" +
							" inner join Participation p on p.actId = a.id and p.role = 101 " +
							" inner join Entity e on e.id = p.entityId " +
							" inner join Participation orgP on orgP.actId = a.id and orgP.role = 102 " +
							" inner join Entity orgE on orgE.id = orgP.entityId" +
							" where ac.name = 'Beratungsgespraech'");
				} else {
				
				 s = con.prepareStatement(
						"select " +
						"						(select intValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Heizung' and af.actId = a.id) heizung," +
						"						(select intValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Strom' and af.actId = a.id) strom," +
						"						e.name, e.id, c.code, a.* from Act a" +
						"						inner join ActClass ac on ac.id = a.actclassid" +
						"						inner join Participation p on p.actid = a.id AND role = 101" +
						"						inner join Entity e on e.id = p.entityId" +
						"						inner join Catalog c on c.id =" +
						"						(select catalogValue from ActField af inner join ActClassField acf on acf.id = af.ClassFieldId where acf.name = 'Gebaeudetyp'" +
						"						and af.actId = (" +
						"						select a.id from Act a" +
						"						inner join ActClass ac on ac.id = a.ActClassId" +
						"						inner join Participation p on p.ActId = a.id" +
						"						where name = 'Gebaeude' and p.role = 103 AND p.entityid = e.id" +
						"						))" +
						"						where ac.Name = 'Energiepass'");
				}
				execSQL(result, s);
				return result ;
			}
		});
	}

	protected String getQuery(ReportDefinition reportDefinition, IAct filters) throws ParserConfigurationException, SAXException, IOException {
		String query = null;
		
		if (reportDefinition.xml != null) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
	
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(reportDefinition.xml)));
			document.getDocumentElement().normalize();
			NodeList params = document.getElementsByTagName("param");
			NodeList queries = document.getElementsByTagName("query");
	
			query = queries.item(0).getTextContent();
		}			
		return query;
	}

	@Override
	public byte[] exportXLS(ISession session, long reportId, IAct filters) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(out); 
		WritableSheet sheet = workbook.createSheet("First Sheet", 0); 

		Catalog c = Locator.getCatalogService().load(session, reportId);
		
		List<HashMap<String, Serializable>> result = execute(session, new ReportDefinition(c), filters);
		
		Iterator<HashMap<String, Serializable>> iter = result.iterator();
		int row = 0;
		int column = 0;
		try {
			while (iter.hasNext()) {
				column = 0;
				HashMap<String, Serializable> next = iter.next();
					if (row == 0) {
						Iterator<Entry<String, Serializable>> eIter = next.entrySet().iterator();
						while (eIter.hasNext()) {
							Entry<String, Serializable> entry = eIter.next();
							Label headerText = new Label(column, 0, entry.getKey());
							sheet.addCell(headerText);
							sheet.addCell(new Label(column, 1, entry.getValue() != null ? entry.getValue().toString() : "<null>"));
							column++;
						}
						row = 1;
					} else {
						Iterator<Serializable> vIter = next.values().iterator();
						while (vIter.hasNext()) {
							Serializable value = vIter.next();
							sheet.addCell(new Label(column++, row, value != null ? value.toString() : "<null>"));
						}
					}
				row++;
			}
			
			workbook.write();
			workbook.close();
			
			return out.toByteArray();
		} catch (WriteException wx) {
			throw new RuntimeException(wx);
		}
	}

	@Override
	public ReportDefinition load(ISession session, Catalog catalog) {
		// TODO Auto-generated method stub
		return null;
	}

	private void execSQL(List<HashMap<String, Serializable>> result,
			PreparedStatement s) throws SQLException {
		ResultSet rs = s.executeQuery();
		ResultSetMetaData md = rs.getMetaData();
		while (rs.next()) {
			HashMap <String, Serializable> record = new HashMap<String, Serializable>();
			for (int i = 0; i < md.getColumnCount(); i++) {
				String columnName = md.getColumnLabel(i+1);
				record.put(columnName, (Serializable) rs.getObject(columnName));
			}
			result.add(record);
		}
		rs.close();
		s.close();
	}

}

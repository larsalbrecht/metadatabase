/**
 * 
 */
package com.lars_albrecht.mdb.main.core.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter;
import com.lars_albrecht.mdb.main.core.models.FileAttributeList;
import com.lars_albrecht.mdb.main.core.models.FileItem;
import com.lars_albrecht.mdb.main.core.models.KeyValue;

/**
 * This is a simple PDF Export.
 * 
 * @author lalbrecht
 * 
 */
public class PDFExport extends AExporter {

	public PDFExport() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter#exportList
	 * (java.io.File, java.util.List, java.util.List)
	 */
	@Override
	public void exportList(final File file, final List<FileItem> fileList, final List<Object> options) {
		try {
			final Document document = new Document();
			document.setPageSize(PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			document.add(this.generateMultiItemTable(fileList));
			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private PdfPTable generateMultiItemTable(final List<FileItem> fileList) {
		// a table with four columns
		final PdfPTable table = new PdfPTable(4);
		table.setHeaderRows(3);
		table.setWidthPercentage(100);
		// the cell object to use
		PdfPCell cell = null;
		Font font = null;

		font = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);

		cell = new PdfPCell(new Phrase("MDB - FileItem export", font));
		cell.setColspan(4);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);

		table.getDefaultCell().setBackgroundColor(BaseColor.BLACK);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.getDefaultCell().setBorderColor(BaseColor.WHITE);
		font = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		table.addCell(new Phrase("Titel", font));
		table.addCell(new Phrase("Typ", font));
		table.addCell(new Phrase("Größe", font));
		table.addCell(new Phrase("Hinzugefügt am", font));

		font = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
		table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		table.getDefaultCell().setBorderColor(BaseColor.BLACK);
		fileList.addAll(fileList);

		for (final FileItem fileItem : fileList) {
			table.addCell(new Phrase(fileItem.getName(), font));
			table.addCell(new Phrase(fileItem.getFiletype(), font));
			table.addCell(new Phrase(Helper.getHumanreadableFileSize(fileItem.getSize()), font));
			table.addCell(new Phrase(Helper.getFormattedTimestamp(fileItem.getCreateTS().longValue(), "dd-mm-yyyy"), font));
		}

		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter#exportItem
	 * (java.io.File, com.lars_albrecht.mdb.main.core.models.FileItem,
	 * java.util.List)
	 */
	@Override
	public void exportItem(final File file, final FileItem fileItem, final List<Object> options) {
		try {
			final Font headlineFont = new Font(FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
			final Font defaultBoldTextFont = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
			final Font defaultTextFont = new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);

			final Document document = new Document();
			document.setPageSize(PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			document.addTitle(fileItem.getName());

			// add title
			Paragraph p = new Paragraph(fileItem.getName(), headlineFont);
			document.add(p);
			// add main file information
			p = new Paragraph(fileItem.getFullpath(), defaultTextFont);
			document.add(p);

			final PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(25);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.setSpacingBefore(10);

			// the cell object to use
			PdfPCell cell = null;

			cell = new PdfPCell(new Phrase("Size", defaultBoldTextFont));
			cell.setBorderColor(BaseColor.WHITE);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(Helper.getHumanreadableFileSize(fileItem.getSize()), defaultTextFont));
			cell.setBorderColor(BaseColor.WHITE);
			table.addCell(cell);

			document.add(table);

			final ArrayList<PdfPTable> tableList = this.generateAttributeTable(fileItem);
			if (tableList != null) {
				for (final PdfPTable pdfPTable : tableList) {
					if (pdfPTable != null) {
						document.add(pdfPTable);
					}
				}
			}

			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<PdfPTable> generateAttributeTable(final FileItem fileItem) {
		ArrayList<PdfPTable> tableList = null;
		PdfPTable table = null;
		if (fileItem.getAttributes().size() > 0) {
			tableList = new ArrayList<PdfPTable>();
			PdfPCell cell = null;
			final Font headlineFont = new Font(FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
			final Font defaultBoldTextFont = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
			final Font defaultTextFont = new Font(FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);

			String currentInfoType = null;
			for (final FileAttributeList attributeList : fileItem.getAttributes()) {
				if (currentInfoType == null || !currentInfoType.equalsIgnoreCase(attributeList.getInfoType())) {
					if (table != null) {
						cell = new PdfPCell(new Phrase(" "));
						cell.setColspan(2);
						table.addCell(cell);
						tableList.add(table);
					}

					table = new PdfPTable(2);
					table.setSpacingBefore(25);
					table.setHeaderRows(1);
					table.setWidthPercentage(100);

					currentInfoType = attributeList.getInfoType();
					attributeList.getKeyValues().get(0).getKey().getInfoType();
					cell = new PdfPCell(new Phrase(attributeList.getInfoType(), headlineFont));
					cell.setColspan(2);
					table.addCell(cell);
				}

				if ((attributeList.getKeyValues() != null) && (attributeList.getKeyValues().size() > 0)) {
					cell = new PdfPCell(new Phrase(" "));
					cell.setColspan(2);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(attributeList.getSectionName(), headlineFont));
					cell.setColspan(2);
					table.addCell(cell);
					for (final KeyValue<String, Object> keyValue : attributeList.getKeyValues()) {
						cell = new PdfPCell(new Phrase(keyValue.getKey().getKey(), defaultBoldTextFont));
						table.addCell(cell);
						cell = new PdfPCell(new Phrase((String) keyValue.getValue().getValue(), defaultTextFont));
						table.addCell(cell);
					}

				}
			}
			tableList.add(table);
		}

		return tableList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter#getExporterName
	 * ()
	 */
	@Override
	public String getExporterName() {
		return this.getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lars_albrecht.mdb.main.core.exporter.abstracts.AExporter#
	 * getExporterDescription()
	 */
	@Override
	public String getExporterDescription() {
		return "Returns a PDF file";
	}

}

package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Scanner;

import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.validator.annotation.Required;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.ja.annotation.SearchField;
import org.ja.annotation.SearchResult;

public class JspCreator {
	PojoHolder pojoHolder=new PojoHolder();
	private StringBuilder tiles;
	private StringBuilder labels;
	private StringBuilder messages;
	private StringBuilder errors;
	private StringBuilder newjsp;
	private StringBuilder editjsp;
	private StringBuilder resultjsp;
	private StringBuilder viewJsp;
	private StringBuilder formJsp;
	private StringBuilder searchJsp;
	private StringBuilder searchResultJsp;
	private Class<?> type;
	public static void main(String[] args) {

		JspCreator rc=new JspCreator();
		rc.createJSP("org.egov.masters.model.AccountEntity");

	}

	public void	createJSP(String fullyQualifiedName)
	{	
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			tiles = new StringBuilder();
			labels = new StringBuilder();
			messages = new StringBuilder();
			errors = new StringBuilder();
			newjsp = new StringBuilder();
			editjsp = new StringBuilder();
			resultjsp = new StringBuilder();
			viewJsp = new StringBuilder();
			formJsp=new StringBuilder();
			 searchJsp=new StringBuilder();
			 searchResultJsp=new StringBuilder();


			Class<?> pojo = pojoHolder.getPojo();

			addTileEntry(pojo);
			//System.out.println(tiles);
			addLabelsEntry(pojo);
			//System.out.println(labels);
			addMessagesEntry(pojo);
			makeNewJsp(pojo);
			makeResultJsp(pojo);
			makeViewJsp(pojo);
			makeEditJsp(pojo);
			makeSearchJSP(pojo);
			
			
			System.out.println(newjsp);

			if(!Utility.WRITETOTEMPFILES)
			{
				if(tiles.length()>0)
				{
				File tilesFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/layout/tiles.xml");
				RandomAccessFile tilesFile=new RandomAccessFile(tilesFileName, "rw");
				tilesFile.seek(tilesFileName.length()-"</tiles-definitions>".getBytes().length-1);
				tilesFile.writeBytes(tiles.toString());
				tilesFile.writeBytes("\n</tiles-definitions>");
				tilesFile.close();
				}

				if(labels.length()>0)
				{
				File labelsFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/resources/messages/labels.properties");
				RandomAccessFile labelsFile=new RandomAccessFile(labelsFileName, "rw");
				labelsFile.seek(labelsFile.length());
				labelsFile.writeBytes(NEWLINE.toString());
				labelsFile.writeBytes(labels.toString());
				labelsFile.close();
				}
				String simpleName = pojo.getSimpleName().toLowerCase();
				File dir=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+simpleName);
				if(!dir.exists())
				{
					dir.mkdirs();

				}
//new jsp		
				if(newjsp.length()>0)
				{
				File newJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-new.jsp");
				PrintWriter newJspFile=new PrintWriter(newJspFileName);
				newJspFile.write(newjsp.toString());
				newJspFile.flush();
				newJspFile.close();
				}
//result jsp
				if(resultjsp.length()>0)
				{
				File resultJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-result.jsp");
				PrintWriter resultJspFile=new PrintWriter(resultJspFileName);
				resultJspFile.write(resultjsp.toString());
				resultJspFile.flush();
				resultJspFile.close();
				}
//view jsp		
				if(viewJsp.length()>0)
				{
				File viewJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-view.jsp");
				PrintWriter viewJspFile=new PrintWriter(viewJspFileName);
				viewJspFile.write(viewJsp.toString());
				viewJspFile.flush();
				viewJspFile.close();
				}
				
				if(formJsp.length()>0)
				{
				File formJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-form.jsp");
				PrintWriter formJspFile=new PrintWriter(formJspFileName);
				formJspFile.write(formJsp.toString());
				formJspFile.flush();
				formJspFile.close();
				}
				if(editjsp.length()>0)
				{
				File editJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-edit.jsp");
				PrintWriter editJspFile=new PrintWriter(editJspFileName);
				editJspFile.write(editjsp.toString());
				editJspFile.flush();
				editJspFile.close();
				}
				
				if(searchJsp.length()>0)
				{
				File searchJspFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/views/"+"/"+simpleName+"/"+simpleName+"-search.jsp");
				PrintWriter searchJspWriter=new PrintWriter(searchJspFileName);
				searchJspWriter.write(searchJsp.toString());
				searchJspWriter.flush();
				searchJspWriter.close();
				}
				
				

				
			}else
			{
				File jsptemp=new File(Utility.PROJECT_WEBHOME+"/jspfiles.jatemp");
				PrintWriter jsptempWriter=new PrintWriter(jsptemp);
				jsptempWriter.write("----------------tiles.xml entry------------------------");
				jsptempWriter.write(tiles.toString());
				jsptempWriter.write("\n\n----------------labels.property entry------------------------");
				jsptempWriter.write(labels.toString());
				jsptempWriter.write("\n\n----------------newJsp------------------------");
				jsptempWriter.write(newjsp.toString());
				jsptempWriter.write("\n\n----------------editjsp------------------------");
				jsptempWriter.write(editjsp.toString());
				jsptempWriter.write("\n\n----------------formJsp------------------------");
				jsptempWriter.write(formJsp.toString());
				jsptempWriter.write("\n\n----------------viewJsp------------------------");
				jsptempWriter.write(viewJsp.toString());
				jsptempWriter.write("\n\n----------------searchJsp------------------------");
				jsptempWriter.write(searchJsp.toString());
				jsptempWriter.write("\n\n----------------searchResultJsp------------------------");
				jsptempWriter.write(searchResultJsp.toString());
				
				
			}

		}catch (Exception e)
		{

		}
	}
	
	
	
	private void makeViewJsp(Class<?> pojo) {
		
		String simpleName = pojo.getSimpleName();
		viewJsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		viewJsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
		String uptoForm=
				"<div class=\"main-content\">"+
						"<div class=\"row\">"+
						"<div class=\"col-md-12\">"+
						"<div class=\"panel panel-primary\" data-collapsed=\"0\">"+
						"<div class=\"panel-heading\">"+
						"<div class=\"panel-title\">"+
						simpleName+
						"</div>"+
						"</div>"+
						"<div class=\"panel-body custom\">";
		viewJsp.append(uptoForm);
		
		
		
		
		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;

		for (Field f:declaredFields)
		{
			//System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			String egFieldType="";
			type = f.getType();
			egFieldType = Utility.findTypes(f);
			SB s=new SB();
			if(i%2==0)	
			{
				viewJsp.append("<div class=\"row add-border\">");
			}
          
			s.a("<div class=\"col-xs-3 add-margin\"><spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+NEWLINE);
			
			s.a("</div>");
			s.a("<div class=\"col-sm-3 add-margin view-content\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+".name}"); // may thing place you have to change to field /getName,getCode etc

			}else if (egFieldType.equals("s"))
			{

				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"}");
				
			}else if(egFieldType.equals("d"))
			{

				s.a("<fmt:formatDate pattern=\"MM/dd/yyyyy\" value=\"${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"} />");
				
			}else if(egFieldType.equals("i")){

				//s.a(" <form:hidden path=\""+f.getName()+"\" />");
			}
			else	
			{

				s.a("${"+Utility.toCamelCase(simpleName)+"."+f.getName()+"}");
				
			}
			
			
			
			
			s.a(""+NEWLINE);
			s.a("</div>");
			if(i%2==1)	
			{
				s.a("</div>"+NEWLINE);
			}

			viewJsp.append(s.str());
			i++;
		}
		
		viewJsp.append("</div></div></div></div>");
		//create buttons
		SB buttons=new SB();
		buttons.a("<div class=\"row text-center\"><div class=\"add-margin\">");
		buttons.a("<a href=\"javascript:void(0)\" class=\"btn btn-default\" onclick=\"self.close()\">Close</a>");
		buttons.a("</div></div>");
		viewJsp.append(buttons.str());
		
		
	}

	private void makeResultJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		resultjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		resultjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
		String uptoForm=
				"<div class=\"main-content\">"+
						"<div class=\"row\">"+
						"<div class=\"col-md-12\">"+
						"<div class=\"panel panel-primary\" data-collapsed=\"0\">"+
						"<div class=\"panel-heading\">"+
						"<div class=\"panel-title\">"+
						simpleName+
						"</div>"+
						"</div>"+
						"<div class=\"panel-body\">";
		resultjsp.append(uptoForm);
		resultjsp.append("<div class=\"panel-title text-center no-float\">");
		resultjsp.append("<strong>${message}</strong>");
		resultjsp.append("</div>");
		resultjsp.append(" </div></div></div></div>");
		SB buttons=new SB();

		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
		resultjsp.append(buttons.str());	

		
	}

	private void makeNewJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		newjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		newjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
	
		newjsp.append("<form:form role=\"form\" action=\"create\" modelAttribute=\""
				+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"form\""+
				" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">"+NEWLINE);	


		//the below content will go to form
		String uptoForm=
				"<div class=\"main-content\">"+
						"<div class=\"row\">"+
						"<div class=\"col-md-12\">"+
						"<div class=\"panel panel-primary\" data-collapsed=\"0\">"+
						"<div class=\"panel-heading\">"+
						"<div class=\"panel-title\">"+
						simpleName+
						"</div>"+
						"</div>"+
						"<div class=\"panel-body\">";
		formJsp.append(uptoForm);
		
		


		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;
		

		for (Field f:declaredFields)
		{
			String requiredMarker="";
			//System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			Required required = f.getDeclaredAnnotation(org.egov.infra.persistence.validator.annotation.Required.class);
			NotBlank notblank = f.getDeclaredAnnotation(org.hibernate.validator.constraints.NotBlank.class);
			NotNull notnull = f.getDeclaredAnnotation(javax.validation.constraints.NotNull.class);
			Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);		
			boolean mandatory=false;
			String egFieldType="";
			type = f.getType();
			egFieldType = Utility.findTypes(f);


			if(required!=null || notblank!=null || notnull!=null)
			{
				mandatory=true;
				System.out.println(mandatory+"-----------"+f.getName());
			}
			SB s=new SB();



			if(i%2==0)	
			{
				s.a("<div class=\"form-group\">"+NEWLINE);
			}
          
			s.a("<label class=\"col-sm-3 control-label text-right\"><spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+NEWLINE);
			if(mandatory)
			{
				s.a("<span class=\"mandatory\"></span>"+NEWLINE);
				requiredMarker="required=\"required\"";
			}
			s.a("</label>");
			s.a("<div class=\"col-sm-3 add-margin\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
				String select="<form:select path=\""+f.getName()+".id\" id=\""+f.getName()+".id\" cssClass=\"form-control\" "+
						"cssErrorClass=\"form-control error\" >"+NEWLINE+
						"<form:option value=\"\"> <spring:message code=\"lbl.select\"/> </form:option>"+NEWLINE+
						"<form:options items=\"${"+Utility.toCamelCase(f.getType().getSimpleName())+"s}\" itemValue=\"id\" itemLabel=\"name\" "+requiredMarker+" />"+NEWLINE+
						"</form:select>"+NEWLINE;
				s.a(select);

			}else if (egFieldType.equals("s"))
			{
				int max=0;
				if(length==null)
				{
					System.err.println("Length is not specified for String field "+f.getName()+NEWLINE);
				}else
				{
					max=length.max();
				}
				s.a("<form:input  path=\""+f.getName()+"\" class=\"form-control text-left patternvalidation\" ");
				s.a("data-pattern=\"alphanumeric\" maxlength=\""+max+"\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
			}else if(egFieldType.equals("d"))
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control datepicker\" data-date-end-date=\"0d\"");
				s.a("  data-inputmask=\"'mask': 'd/m/y'\" "+requiredMarker+"");
				s.a("/>"+NEWLINE);
				
			}else if(egFieldType.equals("i")){

				s.a(" <form:hidden path=\""+f.getName()+"\" />");
				
			}else if(egFieldType.equals("b")){
				s.a(" <form:checkbox path=\""+f.getName()+"\" />");  
			}
			else	
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control text-right patternvalidation\"");
				s.a(" data-pattern=\"number\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
				
			}
			 
			s.a("<form:errors path=\""+f.getName()+"\" cssClass=\"error-msg\" />");
			s.a("</div>");
			if(i%2==1)	
			{
				s.a("</div>"+NEWLINE);
			}

			formJsp.append(s.str());
			i++;
		}
		
		formJsp.append(" <input type=\"hidden\" name=\""+Utility.toCamelCase(simpleName)+"\" value=\"${"+Utility.toCamelCase(simpleName)+".id}\" />");
		//create buttons
		SB buttons=new SB();
		newjsp.append("<%@ include file=\""+simpleName.toLowerCase()+"-form.jsp\"  %>");
		
		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<button type='submit' class='btn btn-primary' id=\"buttonSubmit\"><spring:message code='lbl.create'/></button>")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
	

		newjsp.append(" </div></div></div></div> "+NEWLINE);
		newjsp.append(buttons.str());	
		newjsp.append("</form:form>");
		String script=" <script> "+
		"$('#buttonSubmit').click(function(e){"+
        " if($('form').valid()){"+          
        " }else{"+
         " e.preventDefault();"+
         " }  });"+
          "</script>";
		newjsp.append(script);

	}

	
	private void makeEditJsp(Class<?> pojo) {
		String simpleName = pojo.getSimpleName();
		editjsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		editjsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
	
		editjsp.append("<form:form role=\"form\" action=\"../update\" modelAttribute=\""
				+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"form\""+
				" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">"+NEWLINE);	


		//the below content will go to form
				//create buttons
		SB buttons=new SB();
		editjsp.append("<%@ include file=\""+simpleName.toLowerCase()+"-form.jsp\" %>"+NEWLINE);
		
		editjsp.append("<input type=\"hidden\" name=\""+Utility.toCamelCase(simpleName)+"\" value=\"${"+Utility.toCamelCase(simpleName)+".id}\" />");
		
		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<button type='submit' class='btn btn-primary' id=\"buttonSubmit\"><spring:message code='lbl.update'/></button>")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div>");
		

		editjsp.append(" </div></div></div></div>");
		editjsp.append(buttons.str()+"</form:form>");	
		String script=" <script> "+
		"$('#buttonSubmit').click(function(e){"+
        " if($('form').valid()){"+          
        " }else{"+
         " e.preventDefault();"+
         " }  });"+
          "</script>";
		editjsp.append(script);

  
		
	


	}


	

	private void addMessagesEntry(Class<?> pojo) {
		messages.append("");

	}

	private void addLabelsEntry(Class<?> pojo) {

		String simpleName = pojo.getSimpleName();
		File labelsFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/resources/messages/labels.properties");
		Properties labelProp=new Properties();
		try {
			labelProp.load(new FileInputStream(labelsFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		labels.append(NEWLINE);
		String labelHeader="title."+simpleName.toLowerCase()+".new";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Create New "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".view";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=View "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".edit";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Edit "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".result";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Result "+pojo.getSimpleName()+NEWLINE);
		labelHeader="title."+simpleName.toLowerCase()+".search";
		if(!labelProp.containsKey(labelHeader))
			labels.append(labelHeader+"=Search "+pojo.getSimpleName()+NEWLINE);
		Field[] declaredFields = pojo.getDeclaredFields();
		for (Field f:declaredFields)
		{
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			String key = "lbl."+f.getName().toLowerCase();
			if(!labelProp.containsKey(key))
			{
				type = f.getType();
				if(type.getName().contains("org.egov"))
				{
				labels.append(key+"="+f.getClass().getSimpleName()+NEWLINE);
				}else
				{
					labels.append(key+"="+f.getName()+NEWLINE);
				}
			}
		}

	}
	
	public void makeSearchJSP(Class<?> pojo)
	{
		String simpleName = pojo.getSimpleName();
		searchJsp.append("<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\"%>"+NEWLINE);
		searchJsp.append("<%@ include file=\"/includes/taglibs.jsp\"%>"+NEWLINE);
	
		searchJsp.append("<form:form role=\"form\" action=\"search\" modelAttribute=\""
				+Utility.toCamelCase(simpleName)+"\" id=\""+Utility.toCamelCase(simpleName)+"searchform\""+
				" cssClass=\"form-horizontal form-groups-bordered\" enctype=\"multipart/form-data\">"+NEWLINE);	


		//the below content will go to form
		String uptoForm=
				"<div class=\"main-content\">"+
						"<div class=\"row\">"+
						"<div class=\"col-md-12\">"+
						"<div class=\"panel panel-primary\" data-collapsed=\"0\">"+
						"<div class=\"panel-heading\">"+
						"<div class=\"panel-title\">"+
						"Search"+simpleName+
						"</div>"+
						"</div>"+
						"<div class=\"panel-body\">";
		searchJsp.append(uptoForm);
		
		
		Field[] declaredFields = pojo.getDeclaredFields();
		int i=0;
		

		for (Field f:declaredFields)
		{
			String requiredMarker="";
			//System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			SearchField	isSearchField = f.getDeclaredAnnotation(org.ja.annotation.SearchField.class);
			if(isSearchField!=null)
			{
			Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);	
			
			boolean mandatory=false;
			String egFieldType="";
			type = f.getType();
			egFieldType = Utility.findTypes(f);


			/*if(required!=null || notblank!=null || notnull!=null)
			{
				mandatory=true;
				System.out.println(mandatory+"-----------"+f.getName());
			}*/
			SB s=new SB();



			if(i%2==0)	
			{
				s.a("<div class=\"form-group\">"+NEWLINE);
			}
          
			s.a("<label class=\"col-sm-3 control-label text-right\"><spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+NEWLINE);
			if(mandatory)
			{
				s.a("<span class=\"mandatory\"></span>"+NEWLINE);
				requiredMarker="required=\"required\"";
			}
			s.a("</label>");
			s.a("<div class=\"col-sm-3 add-margin\">"+NEWLINE);
			if(egFieldType.equals("l"))
			{
				String select="<form:select path=\""+f.getName()+".id\" id=\""+f.getName()+".id\" cssClass=\"form-control\" "+
						"cssErrorClass=\"form-control error\" >"+NEWLINE+
						"<form:option value=\"\"> <spring:message code=\"lbl.select\"/> </form:option>"+NEWLINE+
						"<form:options items=\"${"+Utility.toCamelCase(f.getType().getSimpleName())+"s}\" itemValue=\"id\" itemLabel=\"name\" "+requiredMarker+" />"+NEWLINE+
						"</form:select>"+NEWLINE;
				s.a(select);

			}else if (egFieldType.equals("s"))
			{
				int max=0;
				if(length==null)
				{
					System.err.println("Length is not specified for String field "+f.getName()+NEWLINE);
				}else
				{
					max=length.max();
				}
				s.a("<form:input  path=\""+f.getName()+"\" class=\"form-control text-left patternvalidation\" ");
				s.a("data-pattern=\"alphanumeric\" maxlength=\""+max+"\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
			}else if(egFieldType.equals("d"))
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control datepicker\" data-date-end-date=\"0d\"");
				s.a("  data-inputmask=\"'mask': 'd/m/y'\" "+requiredMarker+"");
				s.a("/>"+NEWLINE);
				
			}else if(egFieldType.equals("i")){

				s.a(" <form:hidden path=\""+f.getName()+"\" />");
				
			}
			else	
			{

				s.a(" <form:input path=\""+f.getName()+"\" class=\"form-control text-right patternvalidation\"");
				s.a(" data-pattern=\"number\" "+requiredMarker+" ");
				s.a("/>"+NEWLINE);
				
			}
			 
			s.a("<form:errors path=\""+f.getName()+"\" cssClass=\"error-msg\" />");
			s.a("</div>");
			if(i%2==1)	
			{
				s.a("</div>"+NEWLINE);
			}

			searchJsp.append(s.str());
			i++;
			}
		}
		
		searchJsp.append("<input type=\"hidden\" id=\"mode\" name=\"mode\" value=\"${mode}\"/>");
		
		SB buttons=new SB();
		buttons.a("<div class=\"form-group\">")
		.a("<div class=\"text-center\">")
		.a("<button type='button' class='btn btn-primary' id=\"btnsearch\"><spring:message code='lbl.search'/></button>")
		.a("<a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a>")
		.a("</div></div></div></div></div></div></div></form:form>");
		
		searchJsp.append(buttons.str());
		
		SB resultTable=new SB();
		resultTable.a("<div class=\"row display-hide report-section\">").a(NEWLINE)
		       .a("<div class=\"col-md-12 table-header text-left\">"+simpleName+" Search Result</div>").a(NEWLINE)
		        .a("<div class=\"col-md-12 form-group report-table-container\">").a(NEWLINE)
		        .a("<table class=\"table table-bordered table-hover multiheadertbl\" id=\"resultTable\">").a(NEWLINE);
		
		
		
		
		
		SB tableHeader=new SB();
		tableHeader.a("<thead> <tr> ");
		
		SB cst=new SB();
		for (Field f:declaredFields)
		{
			String requiredMarker="";
			//System.out.println(f.getType().getName());
			//System.out.println();
			if(f.getName().equals("serialVersionUID"))
				continue;
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				continue;
			if(f.getName().equals("id"))
				continue;

			SearchResult isSearchResult = f.getDeclaredAnnotation(org.ja.annotation.SearchResult.class);
			//Length length = f.getDeclaredAnnotation(org.hibernate.validator.constraints.Length.class);	
		   if(isSearchResult!=null){
		   cst.a("<th>"+"<spring:message code=\"lbl."+f.getName().toLowerCase()+"\" />"+"</th>");
		   }
		   
			
		}
		tableHeader.a(cst.str());
		tableHeader.a("</tr></thead>   </table> </div></div>");
		searchJsp.append(resultTable.str()+tableHeader.str());	
		String script=" <script> "+NEWLINE+
		"$('#btnsearch').click(function(e){"+NEWLINE+
        " if($('form').valid()){"+  NEWLINE+        
        " }else{"+NEWLINE+
         " e.preventDefault();"+NEWLINE+
         " }  });"+NEWLINE+
          "</script>"+NEWLINE;
		searchJsp.append(script);
		SB jsImports=new SB();
		
		jsImports.a("<link rel=\"stylesheet\" href=\"<c:url value='/resources/global/css/font-icons/entypo/css/entypo.css' context='/egi'/>\"/>");
		jsImports.a("<link rel=\"stylesheet\" href=\"<c:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/egi'/>\"/>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.columnFilter.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/egi'/>\"></script>");
		jsImports.a("<script src=\"<c:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/egi'/>\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>\"></script>");
		jsImports.a("<script");
		jsImports.a("  src=\"<c:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>\"");
		jsImports.a("  type=\"text/javascript\"></script>");
		jsImports.a("<script type=\"text/javascript\" src=\"<c:url value='/resources/app/js/"+Utility.toCamelCase(simpleName)+"Helper.js'/>\"></script> ");
		
		searchJsp.append(jsImports.str());
		
		
								
		
		
		
	}

	private void addTileEntry(Class<?> pojo) {
		
		File tilesFileName=new File(Utility.PROJECT_WEBHOME+"/src/main/webapp/WEB-INF/layout/tiles.xml");
		String 	tilesContent="";
	    try {
			 	tilesContent = new Scanner(tilesFileName).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		String simpleName = pojo.getSimpleName();
		tiles.append(addSingleEntry(simpleName,"new",tilesContent));
		tiles.append(addSingleEntry(simpleName,"view",tilesContent));
		tiles.append(addSingleEntry(simpleName,"edit",tilesContent));
		tiles.append(addSingleEntry(simpleName,"result",tilesContent));
		tiles.append(addSingleEntry(simpleName,"search",tilesContent));

	}

	private String addSingleEntry(String simpleName,String actionName, String tilesContent) {
		StringBuilder entry=new StringBuilder();
		entry.append(new SB()
		.a("<definition name=\"")
		.a(simpleName.toLowerCase()+"-"+actionName)
		.a("\" extends=\"base_layout\">")
		.a(NEWLINE).str());

		entry.append(new SB()
		.a(TAB+"<put-attribute name=\"page-title\" value=\"title.")
		.a(simpleName.toLowerCase())
		.a("."+actionName)
		.a("\" cascade=\"true\"/>")
		.a(NEWLINE).str());

		entry.append(new SB()
		.a(TAB+"<put-attribute name=\"body\" value=\"")
		.a("/WEB-INF/views/"+simpleName.toLowerCase()+"/"+simpleName.toLowerCase()+"-"+actionName+".jsp\"")
		.a("/>")
		.a(NEWLINE)
		.a("</definition>")
		.a(NEWLINE) 
		.a(NEWLINE).str());
		if(tilesContent.contains(simpleName.toLowerCase()+"-"+actionName))
		{
		if(Utility.WRITETOTEMPFILES)
		{
			System.out.println("tiles.xml entry "+entry.toString());
		}
			return "";
			
		}
		else
			return entry.toString();
	}		
}



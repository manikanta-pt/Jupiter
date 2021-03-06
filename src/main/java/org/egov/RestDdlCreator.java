package org.egov;

import static org.egov.Utility.NEWLINE;
import static org.egov.Utility.TAB;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.ja.annotation.DrillDown;
import org.ja.annotation.DrillDownTable;

public class RestDdlCreator {

	PojoHolder pojoHolder=new PojoHolder();
	private List<String> childList=new ArrayList<String>();
	private Map<String,String> parents=new HashMap<>();
	public static void main(String[] args) {
		
		RestDdlCreator rc=new RestDdlCreator();
		rc.createDdl("org.egov.egf.voucher.domain.model.Voucher");

		
	}
	
	public void createDdl(String fullyQualifiedName)
	{
		
		create(fullyQualifiedName);
		for(int i = 0; i < childList.size(); i++)
		{
			
			create(childList.get(i));
			
		 
		}
		
	}
	
	public void create(String fullyQualifiedName)
	{
		
		
		
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder main=new StringBuilder();  
		StringBuilder seq=new StringBuilder();
		StringBuilder ref=new StringBuilder();
		
		try {
			pojoHolder.loadPojo(fullyQualifiedName);
			Class<?> pojo = pojoHolder.getPojo();
			Field[] declaredFields = pojo.getDeclaredFields();
			System.out.println("Note: If a column does not get added and and only comma is present in line then some thing is missing in that field"
					+ "example if it is referencing object and you missed many to one  etc");
			
			System.out.println("   sdfsd"+pojo.getClass().getAnnotation(Table.class));
			
			//org.egov.tl.domain.entity.Validity.class;
			 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			File fileName=Utility.createNewFile(Utility.SQL_FOLDER+"/V"+simpleDateFormat.format(new Date()) +Utility.CONTEXT.replace("/", "__")+"_"+pojo.getSimpleName()+"_ddl.sql");
		 
			PrintWriter sqlWriter = new PrintWriter(fileName, "UTF-8");
		 
			//String tableName=pojo.getAnnotation(Table.class).name();//this is another place of dependency
			String tableName=Utility.MODULEIDENTIFIER.toLowerCase()+"_"+pojo.getSimpleName().toLowerCase();
			
			String sequenceName = "seq_"+tableName;
			seq.append("create sequence "+sequenceName +";"+NEWLINE);
			
			main.append("Create table "+tableName +"( "+NEWLINE );
			main.append(TAB);
			for(Field f:declaredFields)
			{
				if(f.getName().equals("serialVersionUID"))
					continue;
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				
				
				if(f.isAnnotationPresent(DrillDown.class))
				{
					childList.add(f.getType().getName());
					parents.put(f.getType().getSimpleName(),pojo.getSimpleName());
					continue;
					 
				}
				if(  f.isAnnotationPresent(DrillDownTable.class))
				{
					childList.add(Utility.getEnclosingType(f).getName());
					parents.put(Utility.getEnclosingType(f).getSimpleName(),pojo.getSimpleName());
					continue;
				}
				
				
				String fieldname=f.getName();
				if(f.getAnnotation(Column.class)!=null && 
						f.getAnnotation(Column.class).name()!=null && !f.getAnnotation(Column.class).name().isEmpty())
				{
					fieldname=f.getAnnotation(Column.class).name();
				}
				 
				String egType = Utility.findTypes(f);
				if(f.getAnnotation(OneToMany.class)!=null)
				{
					continue;
				}
				if(f.getAnnotation(ManyToOne.class)!=null)
				{
					
					String fkTable = f.getType().getSimpleName();
					 
					if(f.getAnnotation(JoinColumn.class)!=null)
					{
						fieldname=f.getAnnotation(JoinColumn.class).name();
						 
						if(!f.getAnnotation(JoinColumn.class).nullable())
						{
							main.append("NOT NULL");	
						}
						PojoHolder pojoHolderRef=new PojoHolder();
						pojoHolderRef.loadPojo(f.getType().getName());
						 fkTable = pojoHolderRef.getPojo().getAnnotation(Table.class).name();
						
					}
					main.append(fieldname+" bigint ");
					ref.append("alter table "+tableName+" add constraint fk_"+tableName+"_"+fieldname+" "
							+ " FOREIGN KEY ("+fieldname+") REFERENCES "+fkTable+"(id);"+NEWLINE);
						 
				}
			 if(egType.equals("l"))
				{
					System.out.println(f.getName());
					if (f.getType().isEnum()) //it was a earlier
					{
						 int len=0;
						for(Field ff:f.getType().getDeclaredFields())
						{
							if(!ff.getName().equals("ENUM$VALUES"))
							{
							if(len < ff.getName().length())	
							{
								len=ff.getName().length();
							}
							}
						System.out.println(ff.getName().length()+"   "+ff.getName());
							
						}
						main.append(fieldname+" varchar("+len+")");
					}
					else{
						main.append(fieldname+"Id varchar("+50+")");
								
					}
				
				}
				
				
				else if(egType.equals("s"))
				{
					main.append(fieldname+" varchar(");
					if(f.getAnnotation(Size.class)!=null)
					{
						main.append(f.getAnnotation(Size.class).max()+")");
					}else
					{
						main.append("50)");
						System.err.println("could not find the length for string field "+fieldname);
					}
					
				
				}
				else if(egType.equals("d"))
				{
					main.append(fieldname+" date");
				}
				else if(egType.equals("b"))
				{
					main.append(fieldname+" boolean");
				}else if(egType.equals("c"))
				{
					main.append(fieldname+" varchar(1)");
				}
				else if(egType.equals("i"))
				{
					main.append(fieldname+" bigint");
				}
				
				
				
				else if(egType.equals("n"))
				{
					main.append(fieldname+" ");
					if(f.getType().getName().contains("Integer"))
					{
						main.append("smallint");
					}else if(f.getType().getName().contains("Long"))
					{
						main.append("bigint");
					}else if(f.getType().getName().contains("BigDecimal"))
					{
						main.append("numeric (13,2)");
					}else if(f.getType().getName().contains("Double"))
					{
						main.append("numeric (13,2)");
					}
					else if(f.getType().getName().contains("Double"))
					{
						main.append("numeric (13,2)");
					}
					
						
				}
			    if(f.getAnnotation(NotNull.class)!=null)
				{
					main.append(" NOT NULL");	
				}
				main.append(","+NEWLINE+TAB);
				
			}
			main.append(TAB); 
			if(parents.get(pojo.getSimpleName())!=null)
					{
				main.append(parents.get(pojo.getSimpleName())+"Id varchar(50),\n\t\t");
					}
			main.append("createdby varchar(50),\n\t\t"
					+"createddate timestamp without time zone,\n\t\t"
					+ "lastmodifiedby varchar(50),\n\t\t"
					+ "lastmodifieddate timestamp without time zone,\n\t\t"
					+"tenantId varchar(250),\n\t\t"
					+ "version bigint"+NEWLINE+");"+NEWLINE);
			
			//add pk
			main.append("alter table "+tableName+" add constraint pk_"+tableName+" primary key (id);"+NEWLINE);
			main.append(ref.toString());
			main.append(seq);
			
			//sqlWriter.write(seq.toString());
			sqlWriter.write(NEWLINE);
			sqlWriter.write(main.toString());
			
			
			sqlWriter.flush();
			sqlWriter.close();
			
			
		System.out.println(main.toString());	
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}

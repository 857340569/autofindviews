package autofindviews;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author zhang
 */
public class AutoFindView {
	private static String filePath;
	public enum AutoType{
		Field,Fragment,ViewHolder
	}
	private static AutoType autoType=AutoType.Field;
	public interface OnFoundViewsListener
	{
		void onFound(String viewCodes);
	}
	private static OnFoundViewsListener listener;
//	public static void autoFindView(String filePath,AutoType autoType,OnFoundViewsListener listener)
//	{
//		AutoFindView.filePath=filePath;
//		AutoFindView.autoType=autoType;
//		AutoFindView.listener=listener;
//	}

	public static void autoFindView(InputStream in,AutoType autoType,OnFoundViewsListener listener)
	{
		AutoFindView.autoType=autoType;
		AutoFindView.listener=listener;
		read(in);
	}
    
    private static void read(InputStream in) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
//            InputStream in = AutoFindView.class.getClassLoader().getResourceAsStream(filePath);
//            InputStream in = new FileInputStream(filePath);
            parser.parse(in, new MyHandler());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  private static class MyHandler extends DefaultHandler {
    	private StringBuilder viewFieldBuilder=new StringBuilder();
    	private StringBuilder viewInitBuilder=new StringBuilder();
    	private StringBuilder viewSetTextBuilder=new StringBuilder();
       
    	private long startTime;
        @Override
        public void startDocument() throws SAXException {
        	startTime=System.currentTimeMillis();
        }

        @Override
        public void endDocument() throws SAXException {
        	String viewCodes=viewFieldBuilder.toString();
        	if(autoType!=AutoType.ViewHolder)
        	{
        		viewCodes+="\nprivate void initViews("+(autoType==AutoType.Fragment?"View view":"")+"){\n"+viewInitBuilder.toString()
    			+"}";
        	}
        			
            System.out.println(viewCodes);
            System.out.println();
            System.out.println(viewSetTextBuilder.toString());
            viewCodes+="\n"+viewSetTextBuilder.toString();
            if(listener!=null)
            {
            	listener.onFound(viewCodes);
            }
            
            
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
        	String viewIdStr=findItemViewId(attributes);
        	if(viewIdStr!=null&&!"".equals(viewIdStr.trim()))
        	{
        		if(qName.contains("."))
        		{
        			qName=qName.substring(qName.lastIndexOf(".")+1);
        		}
        		switch (autoType) {
				case Field:
					viewFieldBuilder.append("private "+qName+" "+viewIdStr+";\n");
					viewInitBuilder.append("\t"+viewIdStr+" = findView(R.id."+viewIdStr+");\n");
					break;

				case Fragment:
					viewFieldBuilder.append("private "+qName+" "+viewIdStr+";\n");		
					viewInitBuilder.append("\t"+viewIdStr+" = findView(view,R.id."+viewIdStr+");\n");
					break;
				case ViewHolder:
					viewFieldBuilder.append(qName+" "+viewIdStr+" = CommonViewHolder.findView(convertView,R.id."+viewIdStr+");\n");			
					break;
				}
        		
        		if(qName.contains("TextView"))
        		{
        			viewSetTextBuilder.append(viewIdStr+".setText();\n");
        		}
        		
//        		viewInitBuilder.append(viewIdStr+" = ("+qName+") findViewById(R.id."+viewIdStr+");\n");
        		
        	}
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            super.endElement(uri, localName, qName);
        }

        
        private String findItemViewId(Attributes attrs)
        {
        	 if (attrs == null) 
        		 return null;
        	 String idStr=null;
        	 for (int i = 0; i < attrs.getLength(); i++) {
        		 if(attrs.getValue(i).contains("@+id/"))
        		 {
        			 idStr=attrs.getValue(i);
        			 idStr=idStr.substring(idStr.lastIndexOf("/")+1);
        			 break;
        		 }
             }
        	 return idStr;
        }
   }
}
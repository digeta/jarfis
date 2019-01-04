package jarfis;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;

import com.ibm.icu.util.ULocale;


public class jarfisReport {	
	void buildReport() throws IOException, SemanticException
	{
		EngineConfig conf = null;
        ReportEngine eng = null;
        IReportRunnable design = null;
        IRunAndRenderTask task = null;
        HTMLRenderContext renderContext = null;        
        HashMap contextMap = null;
        HTMLRenderOption htmlRopt = null;
        PDFRenderOption pdfRopt = null;
        
        //Now, setup the BIRT engine configuration. The Engine Home is hardcoded
        //here, this is probably better set in an environment variable or in
        //a configuration file. No other options need to be set
        conf = new EngineConfig();
        conf.setEngineHome("C:/birt-runtime-4_3_0/birt-runtime-4_3_0/ReportEngine");
        
        //Create new Report engine based off of the configuration
        eng = new ReportEngine( conf );
        
        //With our new engine, lets try to open the report design
        try
        {
             design = eng.openReportDesign("src/jarfis/stats.rptdesign"); 
        }
        catch (Exception e)
        {
             System.err.println("Report not Found, are we in release maybe?");
             //e.printStackTrace();
             //System.exit(-1);
        }
        
        try
        {
             design = eng.openReportDesign("stats.rptdesign"); 
        }
        catch (Exception e)
        {
             System.out.println("Report not Found, I dunno what to do :/");
             System.out.println(e.getMessage());
             //e.printStackTrace();
             //System.exit(-1);
        }
        
        //With the file open, create the Run and Render task to run the report
        task = eng.createRunAndRenderTask(design);
        
        //Set Render context to handle url and image locataions, and apply to the
        //task
        renderContext = new HTMLRenderContext();
        renderContext.setImageDirectory("image");
        contextMap = new HashMap();
        contextMap.put( EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderContext );
        task.setAppContext( contextMap );

        //This will set the output file location, the format to rener to, and
        //apply to the task
        /*
        htmlRopt = new HTMLRenderOption();
        htmlRopt.setOutputFileName("c:/temp/output.html");
        htmlRopt.setOutputFormat("html");
        task.setRenderOption(htmlRopt);
        */
        pdfRopt = new PDFRenderOption();
        pdfRopt.setSupportedImageFormats("JPG;PNG;BMP;SVG"); 
        //pdfRopt.setOutputFileName("c:/temp/output.pdf");
        pdfRopt.setOutputFileName("rapor.pdf");
        pdfRopt.setOutputFormat("pdf");
        task.setRenderOption(pdfRopt);
        
        //Cross our fingers and hope everything is set
        try
        {
             task.run();
        }
        catch (Exception e)
        {
             System.out.println("Report task couln't run. We'll call Forest now");
             System.out.println(e.getMessage());
             //e.printStackTrace();
             //System.exit(-1);
        }
        
        //Yeah, we finished. Now destroy the engine and let the garbage collector
        //do its thing
        System.out.println("All went well. Closing program!");
        eng.destroy();
   }
}

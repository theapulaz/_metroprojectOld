package net.minecraft.crash;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.src.CrashReporter;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
    private static final Logger logger = LogManager.getLogger();
    /** Description of the crash report. */
    private final String description;
    /** The Throwable that is the "cause" for this crash and Crash Report. */
    private final Throwable cause;
    /** Category of crash */
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    /** Holds the keys and values of all crash report sections. */
    private final List crashReportSections = new ArrayList();
    /** File of crash report. */
    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private static final String __OBFID = "CL_00000990";
    private boolean reported = false;

    public CrashReport(String p_i1348_1_, Throwable p_i1348_2_)
    {
        this.description = p_i1348_1_;
        this.cause = p_i1348_2_;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server and operating system / java
     * environment
     */
    private void populateEnvironment()
    {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable()
        {
            public String call()
            {
                return "1.7.10";
            }
        });
        this.theReportCategory.addCrashSectionCallable("Operating System", new Callable()
        {
            //private static final String __OBFID = "CL_00001222";
            public String call()
            {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java Version", new Callable()
        {
            //private static final String __OBFID = "CL_00001248";
            public String call()
            {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable()
        {
            //private static final String __OBFID = "CL_00001275";
            public String call()
            {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Memory", new Callable()
        {
            //private static final String __OBFID = "CL_00001302";
            public String call()
            {
                Runtime runtime = Runtime.getRuntime();
                long i = runtime.maxMemory();
                long j = runtime.totalMemory();
                long k = runtime.freeMemory();
                long l = i / 1024L / 1024L;
                long i1 = j / 1024L / 1024L;
                long j1 = k / 1024L / 1024L;
                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable()
        {
            //private static final String __OBFID = "CL_00001329";
            public String call()
            {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List list = runtimemxbean.getInputArguments();
                int i = 0;
                StringBuilder stringbuilder = new StringBuilder();
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    String s = (String)iterator.next();

                    if (s.startsWith("-X"))
                    {
                        if (i++ > 0)
                        {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("%d total; %s", new Object[] {Integer.valueOf(i), stringbuilder.toString()});
            }
        });
        this.theReportCategory.addCrashSectionCallable("AABB Pool Size", new Callable()
        {
            //private static final String __OBFID = "CL_00001355";
            public String call()
            {
                byte b0 = 0;
                int i = 56 * b0;
                int j = i / 1024 / 1024;
                byte b1 = 0;
                int k = 56 * b1;
                int l = k / 1024 / 1024;
                return b0 + " (" + i + " bytes; " + j + " MB) allocated, " + b1 + " (" + k + " bytes; " + l + " MB) used";
            }
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable()
        {
            //private static final String __OBFID = "CL_00001382";
            public String call() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
            {
                return IntCache.getCacheSizes();
            }
        });
        FMLCommonHandler.instance().enhanceCrashReport(this, this.theReportCategory);
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause()
    {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
      public void getSectionsInStringBuilder(StringBuilder par1StringBuilder) {
            if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
                  this.stacktrace = (StackTraceElement[])((StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.crashReportSections.get(0)).func_147152_a(), 0, 1));
            }

            if (this.stacktrace != null && this.stacktrace.length > 0) {
                  par1StringBuilder.append("-- Head --\n");
                  par1StringBuilder.append("Stacktrace:\n");
                  StackTraceElement[] var2 = this.stacktrace;
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                        StackTraceElement var5 = var2[var4];
                        par1StringBuilder.append("\t").append("at ").append(var5.toString());
                        par1StringBuilder.append("\n");
                  }

                  par1StringBuilder.append("\n");
            }

            Iterator var6 = this.crashReportSections.iterator();

            while(var6.hasNext()) {
                  CrashReportCategory var7 = (CrashReportCategory)var6.next();
                  var7.appendToStringBuilder(par1StringBuilder);
                  par1StringBuilder.append("\n\n");
            }

            this.theReportCategory.appendToStringBuilder(par1StringBuilder);
      }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString()
    {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Object object = this.cause;

        if (((Throwable)object).getMessage() == null)
        {
            if (object instanceof NullPointerException)
            {
                object = new NullPointerException(this.description);
            }
            else if (object instanceof StackOverflowError)
            {
                object = new StackOverflowError(this.description);
            }
            else if (object instanceof OutOfMemoryError)
            {
                object = new OutOfMemoryError(this.description);
            }

            ((Throwable)object).setStackTrace(this.cause.getStackTrace());
        }

        String s = ((Throwable)object).toString();

        try
        {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            ((Throwable)object).printStackTrace(printwriter);
            s = stringwriter.toString();
        }
        finally
        {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
      public String getCompleteReport() {
            if (!this.reported) {
                  this.reported = true;
                  CrashReporter.onCrashReport(this, this.theReportCategory);
            }

            StringBuilder var1 = new StringBuilder();
            var1.append("---- Minecraft Crash Report ----\n");
            var1.append("// ");
            var1.append(getWittyComment());
            var1.append("\n\n");
            var1.append("Time: ");
            var1.append((new SimpleDateFormat()).format(new Date()));
            var1.append("\n");
            var1.append("Description: ");
            var1.append(this.description);
            var1.append("\n\n");
            var1.append(this.getCauseStackTraceOrString());
            var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

            for(int var2 = 0; var2 < 87; ++var2) {
                  var1.append("-");
            }

            var1.append("\n\n");
            this.getSectionsInStringBuilder(var1);
            return var1.toString();
      }

    /**
     * Gets the file this crash report is saved into.
     */
    @SideOnly(Side.CLIENT)
    public File getFile()
    {
        return this.crashReportFile;
    }

    /**
     * Saves this CrashReport to the given file and returns a value indicating whether we were successful at doing so.
     */
    public boolean saveToFile(File p_147149_1_)
    {
        if (this.crashReportFile != null)
        {
            return false;
        }
        else
        {
            if (p_147149_1_.getParentFile() != null)
            {
                p_147149_1_.getParentFile().mkdirs();
            }

            try
            {
                FileWriter filewriter = new FileWriter(p_147149_1_);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = p_147149_1_;
                return true;
            }
            catch (Throwable throwable)
            {
                logger.error("Could not save crash report to " + p_147149_1_, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory() 
    {
        return this.theReportCategory;
  }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(String p_85058_1_)
    {
        return this.makeCategoryDepth(p_85058_1_, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
      public CrashReportCategory makeCategoryDepth(String par1Str, int par2) {
            CrashReportCategory var3 = new CrashReportCategory(this, par1Str);
            if (this.field_85059_f) {
                  int var4 = var3.getPrunedStackTrace(par2);
                  StackTraceElement[] var5 = this.cause.getStackTrace();
                  StackTraceElement var6 = null;
                  StackTraceElement var7 = null;
                  int var8 = var5.length - var4;
                  if (var8 < 0) {
                        System.out.println("Negative index in crash report handler (" + var5.length + "/" + var4 + ")");
                  }

                  if (var5 != null && 0 <= var8 && var8 < var5.length) {
                        var6 = var5[var8];
                        if (var5.length + 1 - var4 < var5.length) {
                              var7 = var5[var5.length + 1 - var4];
                        }
                  }

                  this.field_85059_f = var3.firstTwoElementsOfStackTraceMatch(var6, var7);
                  if (var4 > 0 && !this.crashReportSections.isEmpty()) {
                        CrashReportCategory var9 = (CrashReportCategory)this.crashReportSections.get(this.crashReportSections.size() - 1);
                        var9.trimStackTraceEntriesFromBottom(var4);
                  } else if (var5 != null && var5.length >= var4 && 0 <= var8 && var8 < var5.length) {
                        this.stacktrace = new StackTraceElement[var8];
                        System.arraycopy(var5, 0, this.stacktrace, 0, this.stacktrace.length);
                  } else {
                        this.field_85059_f = false;
                  }
            }

            this.crashReportSections.add(var3);
            return var3;
      }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
      private static String getWittyComment() {
            String[] var0 = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

            try {
                  return var0[(int)(System.nanoTime() % (long)var0.length)];
            } catch (Throwable var2) {
                  return "Witty comment unavailable :(";
            }
      }
    /**
     * Creates a crash report for the exception
     */
      public static CrashReport makeCrashReport(Throwable throwable, String par1Str) {
          CrashReport crashreportexeption;
          if (throwable instanceof ReportedException) {
        	  crashreportexeption = ((ReportedException)throwable).getCrashReport();
          } else {
        	  crashreportexeption = new CrashReport(par1Str, throwable);
          }

          return crashreportexeption;
    }
}
package com.landray.kmss.plugin.sign;

import com.landray.kmss.plugin.sign.security.SignatureTool;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SignToolFactory {

   public static SignTool build() {
      try {
         final SignContext context = new SignContext();
         (new Thread(new Runnable() {
            SignatureTool tool = new SignatureTool();

            public void run() {
               context.lock2.lock();

               try {
                  context.condition2 = context.lock2.newCondition();
                  boolean running = true;

                  while(running) {
                     try {
                        context.condition2.await();
                        switch(context.function) {
                        case 1:
                           context.result = this.tool.signInit((Map)context.parameter);
                           break;
                        case 2:
                           context.result = this.tool.sign((File)context.parameter);
                           break;
                        case 3:
                           context.result = this.tool.license((Map)context.parameter);
                           break;
                        case 4:
                           context.result = this.tool.registMachine((Map)context.parameter);
                           break;
                        case 99:
                           running = false;
                        }

                        context.lock1.lock();

                        try {
                           context.function = 0;
                           context.condition1.signalAll();
                        } finally {
                           context.lock1.unlock();
                        }
                     } catch (InterruptedException var11) {
                     } catch (Throwable throwable) {
											 throwable.printStackTrace();
										 }
									}
               } finally {
                  context.lock2.unlock();
               }

            }
         })).start();

         while(context.condition2 == null) {
            try {
               Thread.sleep(1L);
            } catch (InterruptedException var2) {
            }
         }

         return new SignTool() {
            public String signInit(Map _params) throws Throwable {
               try {
                  Map params = new HashMap();
                  params.putAll(_params);
                  return (String)this.call(1, params);
               } catch (Throwable var3) {
                  throw  var3;
               }
            }

            public InputStream sign(File _file) throws Throwable {
               try {
                  File file = new File(_file.getAbsolutePath());
                  return (InputStream)this.call(2, file);
               } catch (Throwable var3) {
                  throw var3;
               }
            }

            public JSONObject license(Map _params) throws Throwable {
               try {
                  Map params = new HashMap();
                  params.putAll(_params);
                  return (JSONObject)this.call(3, params);
               } catch (Throwable var3) {
                  throw var3;
               }
            }

            public String registMachine(Map _params) {
               try {
                  Map params = new HashMap();
                  params.putAll(_params);
                  return (String)this.call(4, params);
               } catch (Throwable var3) {
                  return null;
               }
            }

            public void close() {
               try {
                  this.call(99, (Object)null);
               } catch (Throwable var2) {
               }

            }

            Object call(int function, Object parameter) {
               try {
                  context.lock1.lock();

                  Object var5;
                  try {
                     context.parameter = parameter;
                     context.function = function;
                     context.lock2.lock();

                     try {
                        context.condition2.signalAll();
                     } finally {
                        context.lock2.unlock();
                     }

                     context.condition1.await();
                     var5 = context.result;
                  } finally {
                     context.lock1.unlock();
                  }

                  return var5;
               } catch (Throwable var14) {
                  return null;
               }
            }
         };
      } catch (Throwable var3) {
         return null;
      }
   }

   public static class SignContext {
      Object parameter;
      Object result;
      int function = 0;
      ReentrantLock lock1 = new ReentrantLock();
      Condition condition1;
      ReentrantLock lock2;
      Condition condition2;

      public SignContext() {
         this.condition1 = this.lock1.newCondition();
         this.lock2 = new ReentrantLock();
      }
   }
}

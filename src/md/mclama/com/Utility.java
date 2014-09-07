package md.mclama.com;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utility {
	
	private ModManager McLauncher;
	private String gamePath;

	public Utility(ModManager McLauncher) {
		if(McLauncher!=null){
			this.McLauncher = McLauncher;
			gamePath = McLauncher.gamePath;
		}
	}
	
	private String getJsonFromZip(String path){
		try {
			ZipFile zipFile = new ZipFile(path);
			System.out.println(path.substring(path.lastIndexOf('\\') + 1).replace(".zip","") + "\\info.json");
			zipFile.extractFile(path.substring(path.lastIndexOf('\\') + 1).replace(".zip","") + "\\info.json", System.getProperty("java.io.tmpdir"));
		} catch (ZipException e) {
			System.out.println("Failed to unzip from getJsonFromZip");
		}
		return null;
	}
	
	public String getModVersion(String modName){
		JSONParser parser = new JSONParser();
		 
		try {
			Object obj;
			if(modName.equals("base")){
				obj = parser.parse(new FileReader(gamePath + "\\data\\base\\info.json"));
			} 
			else if(modName.contains(".zip")){
				getJsonFromZip(gamePath + "\\mods\\" + modName);
				obj = parser.parse(new FileReader(System.getProperty("java.io.tmpdir") + modName.replace(".zip", "") + "\\info.json"));
			}	else obj = parser.parse(new FileReader(gamePath + "\\mods\\" + modName + "\\info.json"));
			 
			JSONObject jsonObject = (JSONObject) obj;
			
			return (String) jsonObject.get("version");
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "Error";
	}
	
	public String getModDependency(String modName){
		if(modName.equals("base")) return "";
		JSONParser parser = new JSONParser();
		 
		try {
			Object obj;
			if(modName.contains(".zip")){
				getJsonFromZip(gamePath + "\\mods\\" + modName);
				obj = parser.parse(new FileReader(System.getProperty("java.io.tmpdir") + modName.replace(".zip", "") + "\\info.json"));
			}	else obj = parser.parse(new FileReader(gamePath + "\\mods\\" + modName + "\\info.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			JSONArray jsonArray = (JSONArray) jsonObject.get("dependencies");
			
			
			if(jsonArray!=null) return jsonArray.toString().toLowerCase();
			
		
			
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			return "";
		}
		
		return "";
	}
	
	public boolean canTestVersion(String version){
		
		
		return false;
	}

	public boolean newerVersion(String version, String testv){
		if(testSameVersion(version,testv)) return true;
		String[] str1 = version.split("\\.");
		String[] str2 = testv.split("\\.");
		
		
		if(str1.length==3 && str2.length==3){ //then maybe we have #.#.#
			if(Integer.parseInt(str1[0])>Integer.parseInt(str2[0])){
				System.out.println("str1 higher1");
				return true;
				//if str1 is HIGHER than str2, its newer.
			}
			if(Integer.parseInt(str1[0])<Integer.parseInt(str2[0])){
				System.out.println("str1 lower1");
				return false;
			}
			if(Integer.parseInt(str1[1])>Integer.parseInt(str2[1])){
				System.out.println("str1 higher2");
				return true;
			}
			if(Integer.parseInt(str1[1])<Integer.parseInt(str2[1])){
				System.out.println("str1 lower2");
				return false;
			}
			if(Integer.parseInt(str1[2])>Integer.parseInt(str2[2])){
				System.out.println("str1 higher3");
				return true;
			}
			if(Integer.parseInt(str1[2])<Integer.parseInt(str2[2])){
				System.out.println("str1 lower3");
				return false;
			}
		}
		return false;
	}
	
	public void updateLauncher(){
		try {
//			URL updatePath = new URL(McLauncher.McLauncherPath);
//			ReadableByteChannel rbc = Channels.newChannel(updatePath.openStream());
//			FileOutputStream fos = new FileOutputStream("McLauncher.jar");
//			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//			fos.close();
			String fileName = "McLauncher.jar"; //The file that will be saved on your computer
			 URL link = new URL(McLauncher.McLauncherPath); //The file that you want to download
			 InputStream in = new BufferedInputStream(link.openStream());
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
			 byte[] buf = new byte[1024];
			 int n = 0;
			 while (-1!=(n=in.read(buf)))
			 {
			    out.write(buf, 0, n);
			 }
			 out.close();
			 in.close();
			 byte[] response = out.toByteArray();

			 FileOutputStream fos = new FileOutputStream(fileName);
			 fos.write(response);
			 fos.close();
			 noteInfo("Update");
			JOptionPane.showMessageDialog(null, "Updated launcher, Please re-launch");
			//To be changed to auto-launch, once i figure out how.
			if(McLauncher.tglbtnCloseAfterUpdate.isSelected()){
				 //close launcher if enabled
				System.exit(0);
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isStreamClosed(FileOutputStream out){
	    try {
	        FileChannel fc = out.getChannel();
	        return fc.position() >= 0L; // This may throw a ClosedChannelException.
	    } catch (java.nio.channels.ClosedChannelException cce) {
	        return false;
	    } catch (IOException e) {
	    }
	    return true;
	}

	public boolean testSameVersion(String version, String testv) {
		if(version.equals(testv)) return true;
		return false;
	}
	
	//Code snippet thanks to my friend sn@ke
	public void SendDownloadRequest(String modname){
		URL url = null;
        String webpage = "http://www.mclama.com/McLauncher/ModDownload.php?modname=";
        Scanner scanner = null;
        try {
               
                url = new URL(webpage + modname);
                scanner = new Scanner(url.openStream(), "UTF-8");
                scanner.useDelimiter("\\A");
                String response = scanner.next();
                System.out.println("SendDLReq... " + response);

        } catch (Exception e) {
                e.printStackTrace();
        } finally {
                scanner.close();
        }
	}
	
	public String getModDownloads(){
		URL url = null;
        String webpage = "http://www.mclama.com/McLauncher/GetModList.php";
        Scanner scanner = null;
        try {
               
                url = new URL(webpage);
                scanner = new Scanner(url.openStream(), "UTF-8");
                scanner.useDelimiter("\\A");
                String response = scanner.next();
                System.out.println("GetModDownloads..." + response);
                return response;
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
                scanner.close();
        }
        return "Error";
	}
	
	public String noteInfo(String what){
		if(!System.getProperty("user.home").equals("C:\\Users\\fireblade") //Don't record stats if im... me, So much testing, so much falseness.
				&& McLauncher.tglbtnSendAnonData.isSelected()){  //An option to let players send usage data anonymously.
			URL url = null;
	        String webpage = "http://www.mclama.com/McLauncher/Note" + what;
	        Scanner scanner = null;
	        try {
	               
	                url = new URL(webpage);
	                scanner = new Scanner(url.openStream(), "UTF-8");
	                scanner.useDelimiter("\\A");
	                String response = scanner.next();
	                return response;
	        } catch (Exception e) {
	                e.printStackTrace();
	        } finally {
	                scanner.close();
	        }
	        return "Error";
		}
		return "[log] Lets not update stats if we're the author.";
	}
	
	public String remVer(String str){ //remove version at the end of the mod name
		if(str.contains("_")){
				if(str.contains(".")){
				String[] temp = str.split("_");
				if(temp.length==2){
					System.out.println("DEBUG1: " + temp[0]);
					return temp[0]; //return first split because we had a version
				}
				else if(temp[temp.length-1].contains(".")){ //make sure the mod doesnt have extra _ in the name
					String sb = "";
					for(int i=0; i<temp.length-1; i++){
						sb += temp[i];
						if(i<(temp.length-1)){ 
							sb+="_";
						}
					}
					System.out.println("DEBUG2: " + sb);
					return sb;
				}
			}
		}
		System.out.println("DEBUG3: " + str);
		return str;
	}
	
	public BufferedImage scanImage(String fn) throws IOException {
		BufferedImage img = null;
		URL url = new URL(fn);		
		img = ImageIO.read(url);
		
		return img;
	}
	
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}

	public int getStringWidth(String str) {
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		return (int)(font.getStringBounds(str, frc).getWidth());
	}

	public Boolean IsModInstalled(String mod) {
		boolean foundit=false;
		
		File file = new File(gamePath+"\\mods");
		String[] mods = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
				  File mfile = new File(current, name);
				  if(name.endsWith(".zip") || mfile.isDirectory())
			    return true;
				  else return false;
			  }
			});
		
		for(int i=0; i<mods.length; i++){
			String str = mods[i].replace(".zip","");
			if(remVer(str.substring(str.lastIndexOf('\\') + 1)).equals(mod)){ //if the mod name is found
				return true;
			}
		}
		
		
		if(foundit){
			
			return true;
		}
		return false;
	}
	
}
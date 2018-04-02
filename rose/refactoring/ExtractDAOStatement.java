package refactoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.skku.selab.blp.common.FileParser;
import edu.skku.selab.blp.common.Method;

public class ExtractDAOStatement {
	
	static String path = "./src/edu/skku/selab/blp/db/dao/";
	
	public static void main(String[] args) throws Exception {
		File[] files = new File(path).listFiles();
		String tokens = "ps = analysisDbConnection.prepareStatement(sql)";
		for(int i = 0 ; i<files.length; i++){
			File file = files[i];
			if(!file.getName().contains("DAO") || file.getName().contains("BaseDAO")){
				continue;
			}

			FileParser parser = new FileParser(file);
			
			ArrayList<Method> methodList = parser.getAllMethodList();
			ArrayList<String> methodNameList = new ArrayList<String>();
			for(int j = 0 ; j<methodList.size(); j++){
				String methodName = methodList.get(j).getName();
				if(methodName.charAt(0) >= 'A' && methodName.charAt(0) <= 'Z')
					continue;				
				methodNameList.add(methodName);
			}			
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(path+file.getName()+"2"));
			String str;
			
			while((str=br.readLine())!=null){
				bw.write(str+"\n");
				String flag = isContain(methodNameList,str); 
				if(flag == "")
					continue;
				String line = "";
				while((str = br.readLine()) != null){
					if(!str.contains("\"") || str.contains("//")){
						bw.write(str+"\n");
						continue;
					}
					line = line + str;
					if(str.charAt(str.length()-1) == ';'){
						break;
					}
				}
				line = line.replace("\t", "").replace("String sql ", "").replace("=", "").replace("\"","").replace("+", "");
				
//				System.out.println("sql = \""+line.replace("\t", "")+"\";");
//				System.out.println("ps = analysisDbConnection.prepareStatement(sql);");
//				System.out.println("stmtMap.put(\""+flag+"\",ps);");
				
//				System.out.println("\t\tpublic PreparedStatement "+flag+"{");
//				System.out.println("\t\t\tString sql = "+line.replace("\t", ""));
//				System.out.println("\t\t}");
				
//				System.out.println(flag+" "+line.replace("\t", ""));
				
				while((str = br.readLine()) != null){
					bw.write(str+"\n");
					if(str.contains(tokens)){
						bw.write("			ps = SetOfDAO.stmtMap.get(\""+flag+"\");"+"\n");
						break;
					}							
				}
				bw.write(str+"\n");
			}
			br.close();
			bw.close();
		}		
	}
	
	private static String isContain(ArrayList<String> methodName, String string){
		for(int i = 0 ; i<methodName.size() ; i++){
			if(string.contains(methodName.get(i))){
				return methodName.get(i);
			}
		}		
		return "";
		
	}

}

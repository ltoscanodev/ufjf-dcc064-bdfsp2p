package br.bdfs.client.console;

import br.bdfs.client.BDFSClient;
import br.bdfs.client.command.DfsCommandParser;
import br.bdfs.client.event.send.CpSendEvent.CopyMethod;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.DfsAddress;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ltosc
 */
public class DfsClientConsole 
{
    private final BDFSClient bdfsClient;
    
    public DfsClientConsole(DfsAddress remoteAddress)
    {
        this.bdfsClient = new BDFSClient(remoteAddress);
    }
    
    public void startConsole()
    {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        try
        {
            System.out.print("Usuário: ");
            String username = scanner.nextLine();
            
            System.out.print("Senha: ");
            String password = scanner.nextLine();
            
            System.out.println("Conectando...");
            bdfsClient.login(username, password);
            
            System.out.println();
            System.out.println("============================== BDFS ==============================");
            System.out.println();
            
            do
            {
                try
                {
                    System.out.print(String.format("%s@[%s]> ", username, bdfsClient.pwd()));
                    String cmd = scanner.nextLine();

                    DfsCommandParser dfsCommand = new DfsCommandParser(cmd);

                    switch (dfsCommand.getCommand()) 
                    {
                        case "CP": 
                        {
                            if(dfsCommand.getParamList().containsKey("-L"))
                            {
                                String localPath = dfsCommand.getParamList().get("PARAM_0");
                                String remotePath = dfsCommand.getParamList().get("PARAM_1");
                                
                                if (ObjectChecker.strIsNullOrEmpty(remotePath)) 
                                {
                                    remotePath = PathHelper.concatPath(bdfsClient.pwd(), PathHelper.getName(localPath));
                                } 
                                else if (!remotePath.startsWith(bdfsClient.pwd()))
                                {
                                    remotePath = PathHelper.concatPath(bdfsClient.pwd(), remotePath);
                                }
                                
                                bdfsClient.cp(remotePath, localPath, CopyMethod.LocalCopy);
                            }
                            else if(dfsCommand.getParamList().containsKey("-R"))
                            {
                                String localPath = dfsCommand.getParamList().get("PARAM_1");
                                String remotePath = dfsCommand.getParamList().get("PARAM_0");
                                
                                if (ObjectChecker.strIsNullOrEmpty(remotePath)) 
                                {
                                    remotePath = PathHelper.concatPath(bdfsClient.pwd(), PathHelper.getName(localPath));
                                } 
                                else if (!remotePath.startsWith(bdfsClient.pwd()))
                                {
                                    remotePath = PathHelper.concatPath(bdfsClient.pwd(), remotePath);
                                }
                                
                                bdfsClient.cp(remotePath, localPath, CopyMethod.RemoteCopy);
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        }
                        case "PWD": 
                        {
                            System.out.println(bdfsClient.pwd());
                            break;
                        }
                        case "CD": 
                        {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            
                            if (ObjectChecker.strIsNullOrEmpty(path))
                            {
                                String basePath = PathHelper.basePath(bdfsClient.pwd());
                                
                                if (ObjectChecker.strIsNullOrEmpty(basePath)) 
                                {
                                    path = "~";
                                } 
                                else 
                                {
                                    bdfsClient.setCurrentPath(basePath);
                                    continue;
                                }
                            } 
                            else if (path.startsWith("./"))
                            {
                                path = path.replaceFirst(".", "");
                            }
                            else if (path.equals(".."))
                            {
                                path = PathHelper.previousPath(bdfsClient.pwd());
                            } 
                            else if (!path.startsWith(bdfsClient.pwd())) 
                            {
                                path = PathHelper.concatPath(bdfsClient.pwd(), path);
                            }
                            
                            bdfsClient.cd(path);
                            break;
                        }
                        case "LS": 
                        {
                            String path;

                            if (dfsCommand.getParamList().isEmpty())
                            {
                                path = bdfsClient.pwd();
                            } 
                            else
                            {
                                path = dfsCommand.getParamList().get("PARAM_0");
                                
                                if (!path.startsWith(bdfsClient.pwd())) 
                                {
                                    path = PathHelper.concatPath(bdfsClient.pwd(), path);
                                }
                            }
                            
                            List<String> dirList = bdfsClient.ls(path);
                            
                            if(!ObjectChecker.isNull(dirList))
                            {
                                for (String dir : dirList)
                                {
                                    System.out.println(dir);
                                }
                            }
                            
                            break;
                        }
                        case "MKDIR": 
                        {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            
                            if (!path.startsWith(bdfsClient.pwd()))
                            {
                                path = PathHelper.concatPath(bdfsClient.pwd(), path);
                            }
                            
                            bdfsClient.mkdir(path);
                            break;
                        }
                        case "RMDIR": 
                        {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            
                            if (ObjectChecker.strIsNullOrEmpty(path))
                            {
                                throw new DfsException("Nenhum caminho informado");
                            } 
                            else if (path.equalsIgnoreCase(bdfsClient.pwd())) 
                            {
                                throw new DfsException("Não é possível remover o diretório atual");
                            }
                            else if (!path.startsWith(bdfsClient.pwd())) 
                            {
                                path = PathHelper.concatPath(bdfsClient.pwd(), path);
                            }
                            
                            bdfsClient.rmdir(path);
                            break;
                        }
                        case "RM":
                        {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            
                            if (ObjectChecker.strIsNullOrEmpty(path)) 
                            {
                                throw new DfsException("Nenhum caminho informado");
                            } 
                            else if (!path.startsWith(bdfsClient.pwd())) 
                            {
                                path = PathHelper.concatPath(bdfsClient.pwd(), path);
                            }
                            
                            bdfsClient.rm(path);
                            break;
                        }
                        case "SD": 
                        {
                            if((dfsCommand.getParamList().size() == 2) && dfsCommand.getParamList().containsKey("-C"))
                            {
                                String path = new DfsPath(dfsCommand.getParamList().get("PARAM_0")).toString();
                                bdfsClient.sd("-C", path);
                            }
                            else if((dfsCommand.getParamList().size() == 2) && dfsCommand.getParamList().containsKey("-R"))
                            {
                                String path = new DfsPath(dfsCommand.getParamList().get("PARAM_0")).toString();
                                bdfsClient.sd("-R", path);
                            }
                            else if((dfsCommand.getParamList().size() == 3) && dfsCommand.getParamList().containsKey("-S"))
                            {
                                String path = new DfsPath(dfsCommand.getParamList().get("PARAM_0")).toString();
                                String user = dfsCommand.getParamList().get("PARAM_1");
                                bdfsClient.sd("-S", path, user);
                            }
                            else if((dfsCommand.getParamList().size() == 1) && dfsCommand.getParamList().containsKey("-L"))
                            {
                                List<String> sharedDirList = bdfsClient.sd("-L");
                                
                                for(String sharedDir : sharedDirList)
                                {
                                    System.out.println(sharedDir);
                                }
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        }
                        case "PEER":
                        {
                            if(dfsCommand.getParamList().size() == 1)
                            {
                                String peerAddress = dfsCommand.getParamList().get("PARAM_0");
                                bdfsClient.setRemoteAddress(DfsAddress.fromString(peerAddress));
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        }
                        case "HELP":
                        {
                            System.out.println("cp -L <localPath> [remotePath] - Faz a cópia de um arquivo do sistema local para o sistema remoto");
                            System.out.println("cp -R <remotePath> <localPath> - Faz a cópia de um arquivo do sistema remoto para o sistema local");
                            System.out.println("pwd - Imprime o caminho atual");
                            System.out.println("cd - Define o caminho atual como o diretório raiz");
                            System.out.println("cd <path> - Define o caminho atual como <path>");
                            System.out.println("cd ./<path> - Alterna entre sistema de arquivos do usuário");
                            System.out.println("cd .. - Retorna um diretório no caminho atual");
                            System.out.println("ls - Lista os diretórios/arquivos do caminho atual");
                            System.out.println("ls <path> - Lista os diretórios/arquivos do caminho <path>");
                            System.out.println("mkdir <path> - Cria diretórios/subdiretórios de <path>");
                            System.out.println("rmdir <path> - Remove diretórios/subdiretórios e arquivos de <path>");
                            System.out.println("rm <path> - Remove o arquivo <path>");
                            System.out.println("sd -C <path> - Cria um diretório <path> compartilhado");
                            System.out.println("sd -S <path> <username> - Compartilha o diretório compartilhado <path> com o usuário <username>");
                            System.out.println("sd -R <path> - Remove o diretório compartilhado <path>");
                            System.out.println("sd -L - Lista os diretórios compartilhados");
                            System.out.println("peer <address> - Altera o endereço do peer que o cliente irá se conectar");
                            System.out.println("help - Ajuda sobre os comandos");
                            System.out.println("exit - Sai do sistema");
                            break;
                        }
                        case "EXIT":
                        {
                            running = false;
                            break;
                        }
                        default:
                            System.out.println("Comando desconhecido");
                    }
                }
                catch (DfsException | IOException ex) 
                {
                    System.out.println(ex.getMessage());
                }
            }
            while(running);
            
            System.out.println();
            System.out.println("==================================================================");
            System.out.println();
            
            System.out.println("Desconectando...");
            bdfsClient.logout();
        }
        catch (DfsException | IOException ex) 
        {
            DfsLogger.logError(ex.getMessage());
        }
    }
}

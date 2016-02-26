import hudson.*
import hudson.security.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*

/*
- criação usuarios
- criação de regras
- associação de regras
*/


//Criacao do usuário
def criaUsuario(pUsuario, pSenha){
    try {
        jenkins.model.Jenkins.instance.securityRealm.createAccount(pUsuario, pSenha);
        println "Usuário '" + pUsuario + "' criado com sucesso."    
    }
    catch(Exception e) {
        println "[ERRO]: " + e.message
    }
}

def criaRegraProjeto( pRegraProjeto){

    def estrategiaAutorizacao = Hudson.instance.getAuthorizationStrategy()

    //criacao de role e atribuicao
    if(estrategiaAutorizacao instanceof RoleBasedAuthorizationStrategy){
      RoleBasedAuthorizationStrategy estrategiaRegra = (RoleBasedAuthorizationStrategy) estrategiaAutorizacao

      // Make constructors available
      Constructor[] constrs = Role.class.getConstructors();
      for (Constructor<?> c : constrs) {
        c.setAccessible(true);
      }
      // Make the method assignRole accessible
      Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
      assignRoleMethod.setAccessible(true);

      // Create role
      Set<Permission> permissoes = new HashSet<Permission>();
      permissoes.add(Permission.fromId("hudson.model.Item.Read"));
      permissoes.add(Permission.fromId("hudson.model.Item.Build"));
      permissoes.add(Permission.fromId("hudson.model.Item.Workspace"));
      permissoes.add(Permission.fromId("hudson.model.Item.Cancel"));
      permissoes.add(Permission.fromId("hudson.model.Run.Delete"));
      permissoes.add(Permission.fromId("hudson.model.Run.Update"));


      // The release permission is only available when the release plugin is installed
      /*
      String releasePermission = Permission.fromId("hudson.model.Item.Release");
      if (releasePermission != null) {
        permissoes.add(releasePermission);
      }
      */
      

      Role novaRegra = new Role(pRegraProjeto, "(?i)" + pRegraProjeto + ".*", permissoes);
      estrategiaRegra.addRole(RoleBasedAuthorizationStrategy.PROJECT, novaRegra);

      println "Regra '"+pRegraProjeto+"' criada com sucesso."
    }
    else {
      println "Plugin 'Role Strategy' não encontrado!"
    }

}



def associaRegra(pUsuario, pRegraProjeto){

    def estrategiaAutorizacao = Hudson.instance.getAuthorizationStrategy()

    //criacao de role e atribuicao
    if(estrategiaAutorizacao instanceof RoleBasedAuthorizationStrategy){
      RoleBasedAuthorizationStrategy estrategiaRegra = (RoleBasedAuthorizationStrategy) estrategiaAutorizacao

      // Make constructors available
      Constructor[] constrs = Role.class.getConstructors();
      for (Constructor<?> c : constrs) {
        c.setAccessible(true);
      }
      // Make the method assignRole accessible
      Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
      assignRoleMethod.setAccessible(true);

      // Create role
      Set<Permission> permissoes = new HashSet<Permission>();
      permissoes.add(Permission.fromId("hudson.model.Item.Read"));
      permissoes.add(Permission.fromId("hudson.model.Item.Build"));
      permissoes.add(Permission.fromId("hudson.model.Item.Workspace"));
      permissoes.add(Permission.fromId("hudson.model.Item.Cancel"));
      permissoes.add(Permission.fromId("hudson.model.Run.Delete"));
      permissoes.add(Permission.fromId("hudson.model.Run.Update"));
      
      Role novaRegra = new Role(pRegraProjeto, permissoes);
      estrategiaRegra.addRole(RoleBasedAuthorizationStrategy.PROJECT, novaRegra);

      // assign the role
      estrategiaRegra.assignRole(RoleBasedAuthorizationStrategy.PROJECT, novaRegra, pUsuario);


      pRegraProjeto="usuarios"
      Set<Permission> permissoesGlobal = new HashSet<Permission>();
      permissoesGlobal.add(Permission.fromId("hudson.model.Item.Read"));
      permissoesGlobal.add(Permission.fromId("hudson.model.Item.Build"));
      permissoesGlobal.add(Permission.fromId("hudson.model.Item.Workspace"));
      permissoesGlobal.add(Permission.fromId("hudson.model.Item.Cancel"));
      permissoesGlobal.add(Permission.fromId("hudson.model.Run.Delete"));
      permissoesGlobal.add(Permission.fromId("hudson.model.Run.Update"));

      Role novaRegraGlobal = new Role(pRegraProjeto, permissoesGlobal);
      
      // assign the role
      
      estrategiaRegra.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, novaRegraGlobal, pUsuario);


      println "Regra '"+ pRegraProjeto+"' associada ao usuário '"+pUsuario+"' com sucesso."


    }
    else {
      println "Plugin 'Role Strategy' não encontrado!"
    }

}



criaUsuario("Bill","1")
criaUsuario("Steve","1")
criaUsuario("Linus","1")

criaRegraProjeto("Windows")
criaRegraProjeto("MacOS")
criaRegraProjeto("Linux")

associaRegra("Bill","Windows")
associaRegra("Steve","MacOS")
associaRegra("Linus","Linux")


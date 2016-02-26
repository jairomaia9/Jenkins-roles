import hudson.*
import hudson.security.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*


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



def associaRegra(pUsuario, pRegraProjeto){
	def ldapGroupName = pUsuario
	def projectPrefix = pRegraProjeto
	  
	def authStrategy = Hudson.instance.getAuthorizationStrategy()

	//Criacao do usuário
	jenkins.model.Jenkins.instance.securityRealm.createAccount("user1", "password123")

	//criacao de role e atribuicao
	if(authStrategy instanceof RoleBasedAuthorizationStrategy){
	  RoleBasedAuthorizationStrategy roleAuthStrategy = (RoleBasedAuthorizationStrategy) authStrategy

	  // Make constructors available
	  Constructor[] constrs = Role.class.getConstructors();
	  for (Constructor<?> c : constrs) {
	    c.setAccessible(true);
	  }
	  // Make the method assignRole accessible
	  Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
	  assignRoleMethod.setAccessible(true);

	  // Create role
	  Set<Permission> permissions = new HashSet<Permission>();

	  switch(pRegraProjeto) {
	  	case "buider":
	  		permissions.add(Permission.fromId("hudson.model.Item.Read"));
			permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Configure"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			permissions.add(Permission.fromId("hudson.model.Item.Cancel"));
			permissions.add(Permission.fromId("hudson.model.Run.Delete"));
			permissions.add(Permission.fromId("hudson.model.Run.Update"));
	  		break;

	  	case "developer":
	  		permissions.add(Permission.fromId("hudson.model.Item.Read"));
			permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Configure"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			permissions.add(Permission.fromId("hudson.model.Item.Cancel"));
			permissions.add(Permission.fromId("hudson.model.Run.Delete"));
			permissions.add(Permission.fromId("hudson.model.Run.Update"));
	  		break;

	  	default:
			permissions.add(Permission.fromId("hudson.model.Item.Read"));
			permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Configure"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			permissions.add(Permission.fromId("hudson.model.Item.Cancel"));
			permissions.add(Permission.fromId("hudson.model.Run.Delete"));
			permissions.add(Permission.fromId("hudson.model.Run.Update"));
	  		break;
	  }
	  

	  Role newRole = new Role(projectPrefix, projectPrefix + ".*", permissions);
	  roleAuthStrategy.addRole(RoleBasedAuthorizationStrategy.PROJECT, newRole);

	  // assign the role
	  roleAuthStrategy.assignRole(RoleBasedAuthorizationStrategy.PROJECT, newRole, ldapGroupName);
	  
	  println "OK"
	}
	else {
	  println "Role Strategy Plugin not found!"
	}
}


criaUsuario("Bill","1")
criaUsuario("Steve","1")
criaUsuario("Linus","1")


associaRegra("Bill","Windows")
associaRegra("Steve","MacOS")
associaRegra("Linus","Linux")


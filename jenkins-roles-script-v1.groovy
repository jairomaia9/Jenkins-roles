import hudson.*
import hudson.security.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*
import jenkins.model.Jenkins;
  
  
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



def associaRegra(pUsuario, pProjeto, pRegraProjeto){
	def ldapGroupName = pUsuario
	def projectPrefix = pProjeto
	  
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

		//REGRAS GLOBAIS


		RoleMap roles = roleAuthStrategy.getRoleMap(RoleBasedAuthorizationStrategy.GLOBAL);

		Role targetRole = roles.getRole("usuarios");

		if (targetRole != null) {
		  roleAuthStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, targetRole, ldapGroupName);
		}
		else{
			println "ERRO: Regra geral nao encontrada"
		}	  


		// REGRAS DE PROJETO
		Set<Permission> permissions = new HashSet<Permission>();

		switch(pRegraProjeto) {
			case "buider":
				permissions.add(Permission.fromId("hudson.model.Item.Create"));
				permissions.add(Permission.fromId("hudson.model.Item.Delete"));
				permissions.add(Permission.fromId("hudson.model.Item.Configure"));
				permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			break;

			case "developer":
				permissions.add(Permission.fromId("hudson.model.Item.Build"));
			break;

			case "tester":
				permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			permissions.add(Permission.fromId("hudson.model.SCM.Tag"));
				break;

			case "manager":

				permissions.add(Permission.fromId("hudson.model.Item.Create"));
				permissions.add(Permission.fromId("hudson.model.Item.Delete"));
				permissions.add(Permission.fromId("hudson.model.View.Create"));
				permissions.add(Permission.fromId("hudson.model.View.Delete"));
				permissions.add(Permission.fromId("hudson.model.View.Configure"));

			break;

			case "reader":
				permissions.add(Permission.fromId("hudson.model.Item.Read"));
			break;

			default:
			permissions.add(Permission.fromId("hudson.model.Item.Read"));
			permissions.add(Permission.fromId("hudson.model.Item.Build"));
			permissions.add(Permission.fromId("hudson.model.Item.Configure"));
			permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
			permissions.add(Permission.fromId("hudson.model.Item.Cancel"));
			permissions.add(Permission.fromId("hudson.model.Run.Delete"));
			permissions.add(Permission.fromId("hudson.model.Run.Update"));
		}


		Role newRole = new Role(projectPrefix, "(?i)" + projectPrefix + ".*", permissions);
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


associaRegra("Bill","Windows","")
associaRegra("Steve","MacOS","")
associaRegra("Linus","Linux","")


/*
Sugestão de criação de regras
========================================

fonte: https://documentation.cloudbees.com/docs/cje-user-guide/rbac-sect-sample-configs.html

*/

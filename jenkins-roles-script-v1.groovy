import hudson.*
import hudson.security.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*
import jenkins.model.Jenkins;




  
  
//Criacao do usuário
def criaUsuario(pUsuario, pSenha){
	def role_USUARIO="usuarios";

    try {
        jenkins.model.Jenkins.instance.securityRealm.createAccount(pUsuario, pSenha);
        println "Usuário '" + pUsuario + "' criado com sucesso."   ;
        associaRegraGlobal(pUsuario,role_USUARIO);
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
		println "Role Strategy Plugin nao encontrado!"
	}
}



def associaRegraProjeto(pUsuario, pRegraProjeto){
	
	def authStrategy = Hudson.instance.getAuthorizationStrategy()

	
	//criacao de role e atribuicao
	if(authStrategy instanceof RoleBasedAuthorizationStrategy){
		RoleBasedAuthorizationStrategy roleAuthStrategy = (RoleBasedAuthorizationStrategy) authStrategy

		RoleMap roles = roleAuthStrategy.getRoleMap(RoleBasedAuthorizationStrategy.PROJECT);

		Role targetRole = roles.getRole(pRegraProjeto);

		if (targetRole != null) {
			roleAuthStrategy.assignRole(RoleBasedAuthorizationStrategy.PROJECT, targetRole, pUsuario);
			println "Regra '"+pRegraProjeto+"' associada ao usuario ."
		}
		else{
			println "ERRO: Regra '"+pRegraProjeto+"' nao encontrada."
		}	  

		println "OK"
	}
	else {
		println "Role Strategy Plugin nao encontrado!"
	}
}

def associaRegraGlobal(pUsuario, pRegraProjeto){
	
	def authStrategy = Hudson.instance.getAuthorizationStrategy()

	
	//criacao de role e atribuicao
	if(authStrategy instanceof RoleBasedAuthorizationStrategy){
		RoleBasedAuthorizationStrategy roleAuthStrategy = (RoleBasedAuthorizationStrategy) authStrategy

		RoleMap roles = roleAuthStrategy.getRoleMap(RoleBasedAuthorizationStrategy.GLOBAL);

		Role targetRole = roles.getRole(pRegraProjeto);

		if (targetRole != null) {
			roleAuthStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, targetRole, pUsuario);
			println "Regra '"+pRegraProjeto+"' associada ao usuario ."
		}
		else{
			println "ERRO: Regra '"+pRegraProjeto+"' nao encontrada."
		}	  

		println "OK"
	}
	else {
		println "Role Strategy Plugin nao encontrado!"
	}
}

def criaRegraProjeto(pProjeto, pRegraProjeto){

	  
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

		
		// REGRAS DE PROJETO
		Set<Permission> permissions = new HashSet<Permission>();

		switch(pRegraProjeto) {
			case "builder":
				permissions.add(Permission.fromId("hudson.model.Item.Read"));
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

			case "default":
				permissions.add(Permission.fromId("hudson.model.Item.Read"));
				permissions.add(Permission.fromId("hudson.model.Item.Build"));
				permissions.add(Permission.fromId("hudson.model.Item.Configure"));
				permissions.add(Permission.fromId("hudson.model.Item.Workspace"));
				permissions.add(Permission.fromId("hudson.model.Item.Cancel"));
				permissions.add(Permission.fromId("hudson.model.Run.Delete"));
				permissions.add(Permission.fromId("hudson.model.Run.Update"));
				break;

			default:
				println "Regra '" + pRegraProjeto +"' não encontrada";
				pRegraProjeto="";

		}
		if (pRegraProjeto!=""){
			Role newRole = new Role(pProjeto+"-"+pRegraProjeto, "(?i)" + pProjeto + ".*", permissions);
			roleAuthStrategy.addRole(RoleBasedAuthorizationStrategy.PROJECT, newRole);

			// assign the role
			

			println "Regra criada com sucesso"			
		}


	}
	else {
		println "Role Strategy Plugin nao encontrado!"
	}
}

criaUsuario("Bill","1")
criaUsuario("Steve","1")
criaUsuario("Linus","1")

criaRegraProjeto("Windows", "default")
criaRegraProjeto("Windows", "builder")
criaRegraProjeto("Windows", "reader")

criaRegraProjeto("MacOS", "default")
criaRegraProjeto("MacOS", "builder")
criaRegraProjeto("MacOS", "reader")

criaRegraProjeto("Linux", "default")
criaRegraProjeto("Linux", "builder")
criaRegraProjeto("Linux", "reader")

associaRegraProjeto("Bill","Windows-default")
associaRegraProjeto("Steve","MacOS-builder")
associaRegraProjeto("Linus","Linux-reader")




/*
Sugestão de criação de regras
========================================

fonte: https://documentation.cloudbees.com/docs/cje-user-guide/rbac-sect-sample-configs.html

*/

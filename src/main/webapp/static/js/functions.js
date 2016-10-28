var users;
var apiroot = "/AnnetteUserDbWebApp"
var transactions;

$(document).ready(function(){
	
	//read the user list from the web service
	//users is is taken from USERDBApi
	var url = apiroot + "/users";
	var data = {};
	$.getJSON(url, data, function(data, status, xhr){
		users = data;
		
		//update the ui
		
		populateList();
	});
	
	$("#addTransaction").on("click", function(){
		
		$("#dlgAddTransaction").dialog({
			title: "Add a Transaction",
			modal:true,
			buttons: {
				Ok: function() {
					var url = apiroot + "/users/user/" + $("#id").val() + "/transactions/transaction";
					var method = "PUT";
					var data = {
							id: -1,
							userId: parseInt($("#id").val()),
							description: $("#dlgDescription").val() ,
							transactionDate: $("#dlgTransactionDate").val() ,
							amount: parseFloat($("#dlgAmount").val())
					} // End of data
					$.ajax(url, {
						data:JSON.stringify(data),
						contentType: "application/json",
						method:method,
						success: function(){
							showStatusMessage("Transaction added");
							$("#dlgAddTransaction").dialog("close");
						}, //End of success
						error: function(){
							showStatusMessage("Error adding transaction");
							$("#dlgAddTransaction").dialog("close");
						}
					}); //End of ajax
					
					$(this).dialog("close");
				}, //End of ok Button
				Cancel: function(){
					$(this).dialog("close");
				} //End of cancel
				
			} //End of buttons
		
			}); //End of dialog
		
	}); //End of add transaction
	
	$("#leftUserList").on("change", function(){
		var id = this.value;
		var user = findUser(id);
		showUser(user);
		
	});
	
	$(document).on("click", ".deleteTransaction", function(event){
		if ($("#" + event.target.id).hasClass("btnDisabled")){
			return;
		}
		
		$("#dlgConfirm").dialog({
		modal:true,
			buttons: {
				"Ok": function() {
					var tid = event.target.id.split("_")[1];
					var uid = $("#id").val();
					
					var url = apiroot + "/users/user/" + uid + "/transactions/transaction/" + tid;
					var method = "DELETE";
					var data = {};
					
					$.ajax(url, {
						data:JSON.stringify(data),
						contentType: "application/json",
						method:method,
						success: function(){
							showStatusMessage("Transaction deleted");
						},
						error: function(){
							showStatusMessage("Error deleting transaction");
							$("#dlgConfirm").dialog("close");
						}
						
					});
					$("#dlgConfirm").dialog("close");
				},
				"Cancel": function(){
					$("#dlgConfirm").dialog("close");	
				}
				}
		
		}); //End of dialog confirm
	}); //End of delete transaction
	
	$(document).on("click", ".editTransaction", function(event){
		if ($("#" + event.target.id).hasClass("btnDisabled")){
			return;
		}
		
				
		//add controls to edit
		var tid = event.target.id.split("_")[1];
		transaction = findTransaction(tid);

		//3rd td tag is the description
		$("#tr_" + tid + " td:nth-child(3)").html(
				"<input id=\"txDescription_" + tid + "\" type=\"text\"" +
				"value=\"" + transaction.description + "\">");
		$("#tr_" + tid + " td:nth-child(4)").html(
				"<input id=\"txTransactionDate_" + tid + "\" type=\"text\"" +
				"value=\"" + transaction.transactionDate + "\" size=\"8\">");
		$("#txTransactionDate_" + tid).datepicker({ dateFormat: 'yy-mm-dd',
							changeYear:true,
							changeMonth:true,
							yearRange: "-120:+0"
								});
		$("#tr_" + tid + " td:nth-child(5)").html(
				"<input id=\"txAmount_" + tid + "\" type=\"text\"" +
				"value=\"" + transaction.amount + "\" size=\"8\">");
		
		$("#et_" + tid).addClass("btnDisabled");
		$("#st_" + tid).removeClass("btnDisabled");
		$("#ct_" + tid).removeClass("btnDisabled");
		
		
	}); //End of end Transaction
	
	
	$(document).on("click", ".saveTransaction", function(event){
		if ($("#" + event.target.id).hasClass("btnDisabled")){
			return;
		}
		
		
		var tid = event.target.id.split("_")[1];
		transaction = findTransaction(tid);
		transaction.description = $("#txDescription_" + tid).val();
		transaction.transactionDate = $("#txTransactionDate_" + tid).val();
		transaction.amount = $("#txAmount_" + tid).val();
		
		var url = apiroot + "/users/user/" + $("#id").val() + "/transactions/transaction";
		var method = "POST";
		
		$.ajax(url, {
			data:JSON.stringify(transaction),
			contentType: "application/json",
			method:method,
			success: function(){
				$("#tr_" + tid + " td:nth-child(3)").html(transaction.description);
				$("#tr_" + tid + " td:nth-child(4)").html(transaction.transactionDate);
				$("#tr_" + tid + " td:nth-child(5)").html(transaction.amount);
				
				showStatusMessage("Saved");				
			},
			error: function(){
				showStatusMessage("Error saving transaction");
			}
		});
		$("#et_" + tid).removeClass("btnDisabled");
		$("#st_" + tid).addClass("btnDisabled");
		$("#ct_" + tid).addClass("btnDisabled");
	}); //End of save transaction
	
	
	$(document).on("click", ".cancelTransaction", function(event){
		if ($("#" + event.target.id).hasClass("btnDisabled")){
			return;
		}
				
		var tid = event.target.id.split("_")[1];
		transaction = findTransaction(tid);
		$("#tr_" + tid + " td:nth-child(3)").html(transaction.description);
		$("#tr_" + tid + " td:nth-child(4)").html(transaction.transactionDate);
		$("#tr_" + tid + " td:nth-child(5)").html(transaction.amount);
		
		$("#et_" + tid).removeClass("btnDisabled");
		$("#st_" + tid).addClass("btnDisabled");
		$("#ct_" + tid).addClass("btnDisabled");
	}); //End of cancelTransaction
	

		
	$(document).on("click", "#editUser", function(){
		$("#firstName").attr("readonly", false);
		$("#lastName").attr("readonly", false);
		$("#registered").attr("disabled", false);
		$("#dateOfBirth").attr("readonly", false);
		
		if($("#editUser").hasClass("btnDisabled")){
			return;
		}
		$("#editUser").addClass("btnDisabled");
		$("#saveUser").removeClass("btnDisabled");
		$("#cancelUserEdit").removeClass("btnDisabled");
		
	}); //End of edit user
	
	$(document).on("click", "#saveUser", function(){
		
		if($("#saveUser").hasClass("btnDisabled")){
			return;
		}
		
		$("#editUser").removeClass("btnDisabled");
		$("#saveUser").addClass("btnDisabled");
		$("#cancelUserEdit").addClass("btnDisabled");
		
		var id = $("#id").val();
		var user = findUser(id);
		user.firstName = $("#firstName").val();
		user.lastName = $("#lastName").val();
		user.registered = $("#registered").prop("checked");
		user.dateOfBirth = $("#dateOfBirth").val();
		
		var url = apiroot + "/users/user";
		var method = "POST";
		
		$.ajax(url, {
			data:JSON.stringify(user),
			contentType: "application/json",
			method:method,
			success: function(){
				showStatusMessage("User Saved");
				$("#firstName").attr("readonly", true);
				$("#lastName").attr("readonly", true);
				$("#registered").attr("disabled", true);
				$("#dateOfBirth").attr("readonly", true);
			},
			error: function(){
				showStatusMessage("Error");		
			}
		}); //End of ajax
	}); //End of save user
	
	$(document).on("click", "#cancelUserEdit", function(){
		
		if($("#cancelUserEdit").hasClass("btnDisabled")){
			return;
		}
		
		$("#editUser").removeClass("btnDisabled");
		$("#saveUser").addClass("btnDisabled");
		$("#cancelUserEdit").addClass("btnDisabled");
		
		var id = $("id").val();
		var user = findUser(id);
		showUser(user);
		
		$("#firstName").attr("readonly", true);
		$("#lastName").attr("readonly", true);
		$("#registered").attr("disabled", true);
		$("#dateOfBirth").attr("readonly", true);
	}); //End of cancelUserEdit
	
	$(document).on("click", "#deleteUser", function(){
		
		if($("#deleteUser").hasClass("btnDisabled")){
			return;
		}
		$("#dlgConfirm").dialog({
			modal:true,
				buttons: {
					"Ok": function() {
						var uid = $("#id").val();
						
						var url = apiroot + "/users/user/" + uid;
						var method = "DELETE";
						var data = {};
						
						$.ajax(url, {
							data:JSON.stringify(data),
							contentType: "application/json",
							method:method,
							success: function(){
								showStatusMessage("User deleted");
							},//End of success function
							error: function(){
								showStatusMessage("Error deleting User");
								$("#dlgConfirm").dialog("close");
							} //End of error
							
						}); //End of ajax
						$("#dlgConfirm").dialog("close");
						deleteUserFromList(uid);
						populateList();
					}, //End of ok
					"Cancel": function(){
						$("#dlgConfirm").dialog("close");
						
					} //End of cancel 
				} //End of buttons
			
			}); //End of dialog
	}); //End of Delete User
});

//*************************************************************************
function showStatusMessage(message){
	$("#statusMessage").show();
	$("#statusMessage").html(message);
	$("#statusMessage").fadeOut(2500, function(){
		$("#statusMessage").html("");
	});//end of fadeout function
	
}//End of function

//*************************************************************************


//*************************************************************************
function showTransactions(id){
	
	url = apiroot + "/users/user/" + id + "/transactions";
	data = {};
	transactions = {};
	$.getJSON(url, data, function(data, status, xhr){
		
		transactions = data;
		$("#bottomRightTransactions").empty();
		$.each(data, function(index, transaction){
		
			addTransactionToTable($("#bottomRightTransactions"), transaction);
		});//End of for each
	});//End of getJson
}//End of function
//*************************************************************************

//*************************************************************************
function addTransactionToTable(table, transaction){
	table.append("<tr id=\"tr_" + transaction.id + "\">" +
			"<td>" + transaction.id + "</td> " +
			"<td>"  + transaction.userId + "</td>" +
			"<td>"  + transaction.description + "</td>" +
			"<td>"  + transaction.transactionDate + "</td>" +
			"<td>"  + transaction.amount + "</td>" +
			"<td id=\"dt_"+ transaction.id + "\" class='deleteTransaction txButton fa fa-trash-o'></td>" +
			"<td id=\"et_"+ transaction.id + "\" class='editTransaction txButton fa fa-edit'></td>" +
			"<td id=\"st_"+ transaction.id + "\" class='saveTransaction txButton fa fa-floppy-o btnDisabled'></td>" +
			"<td id=\"ct_"+ transaction.id + "\" class='cancelTransaction txButton fa fa-undo btnDisabled'></td>" +
			"</tr>");
}//End of function
//*************************************************************************


//*************************************************************************
function showUser(user){
	$("#id").val(user.id);
	$("#firstName").val(user.firstName);
	$("#lastName").val(user.lastName);
	$("#registered").prop("checked", user.registered);
	$("#dateOfBirth").val(user.dateOfBirth);
}//End of function
//*************************************************************************


//*************************************************************************
function findUser(id) {
	
	for (var i=0; i<users.length; i++){
		if (users[i].id == id){
			return users[i];
		}//End of if
		
	}//End of for loop
	
}//End of function
//*************************************************************************


//*************************************************************************
function deleteUserFromList(id){
	
	for (var i=0; i<users.length; i++){
		if(users[i].id == id){
			users.splice(i, 1);
		}//End of if
		
	}//End of for loop
	
}//End of function

//*************************************************************************


//*************************************************************************
function findTransaction(tid) {
	
	for (var i=0; i<transactions.length; i++){
		if (transactions[i].id == tid){
			return transactions[i];
		}//End of if loop
	}//End of for loop
}//End of function
//*************************************************************************

//*************************************************************************
function populateList(){
	//alert(users[0].firstName);
	
	$("#leftUserList").empty();
	
	$.each(users, function(index, user){
		
		$("#leftUserList").append("<option value='" + user.id + "'>" + 
						user.firstName + " " + user.lastName + 
						"</option>");
	}); //End of for Each
	
	$("#leftUserList option:first").prop("selected", true);
	
	var user = users[0];
	showUser(user);
	showTransactions(user.id);
}//End of Function
//*************************************************************************
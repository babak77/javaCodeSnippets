<%-- 
    Document   : index
    Created on : 26.07.2019, 10:17:56
    Author     : bhashemi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap-theme.min.css" rel="stylesheet" type="text/css"/>

        <!-- For DataTable  -->
        <script src="js/jquery-3.3.1.js" type="text/javascript"></script>
        <script src="js/jquery.dataTables.js" type="text/javascript"></script>
        <script src="js/dataTables.bootstrap4.js" type="text/javascript"></script>
        <link href="css/dataTables.bootstrap4.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" type="text/css"/>

        <!-- Include jQuery -->
        <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <!-- Bootstrap Date-Picker Plugin -->
        <script src="js/bootstrap-datepicker.min.js" type="text/javascript"></script>
        <link href="css/bootstrap-datepicker3.css" rel="stylesheet" type="text/css"/>

        <!-- Special version of Bootstrap that only affects content wrapped in .bootstrap-iso -->
        <link href="css/bootstrap-iso.css" rel="stylesheet" type="text/css"/>

        <!--Font Awesome (added because you use icons in your prepend/append)-->
        <link href="css/font-awesome.min.css" rel="stylesheet" type="text/css"/> 
        <link href="css/style.css" rel="stylesheet" type="text/css"/>

        <script src="js/custom.js" type="text/javascript"></script>
    </head>
    <body>
        <div class="jumbotron jumbotron-fluid">
            <div class="container">
                <h1 class="display-4">Karaf Services</h1>
                <p class="lead"></p>
            </div>
        </div>
        <h1>Report</h1>

        <div class='alert-message hidden'> <!-- SHOW MESSAGES HERE --> </div>


        <!--        <table class="table table-striped" id="resultTable" >
                    
                </table>-->

        <div class="tableContainer">
            <table id="resultTable" class="table table-striped table-bordered" cellspacing="0" style="width:100%">
                <thead id="tableHeader">
                    <tr>
                        <th colspan="2">OSGI</th>
                        <th colspan="2">PROD</th>
                        <th colspan="3">TEST</th>
                    </tr>
                    <tr>
                        <th id="jobNameCol">Job Name</th>
                        <th id="groupIdCol">Group ID</th>
                        <th id="ProdRuntime1Col">Runtime 1</th>
                        <th id="ProdRuntime2Col">Runtime 2</th>
                        <th id="TestRuntime1Col">Runtime 1</th>
                        <th id="TestRuntime2Col">Runtime 2</th>
                        <th id="TestRuntime3Col">Runtime 3</th>

                    </tr>
                </thead>
                <tbody id="jobsTableBody">

                </tbody>
            </table>

        </div>
        <div class="container buttons_container">
            <div class="row">
                <div class="col-sm">
                    <button class='btn btn-danger btn-remove-group' type='button'><span class='glyphicon glyphicon-minus'>Delete</span></button>
                </div>
                <div class="col-sm">
                    <button class='btn btn-success btn-remove-group' type='button'><span class='glyphicon glyphicon-minus'>Add Service</span></button>
                </div>
                <div class="col-sm">
                    <button class='btn btn-secondary btn-remove-group' type='button'><span class='glyphicon glyphicon-minus'>Cancel</span></button>
                </div>
                <div class="col-sm">
                    <button class='btn btn-success btn-save-group' type='button'><span class='glyphicon glyphicon-minus'>Save</span></button>
                </div>
            </div>
        </div>
        <div class="modal" id="loader"><!-- Place at bottom of page --></div>  
    </body>

    <Script>
        $(function ()
        {
        populateTableData();
        $(document).on('click', '.btn-save-group', function (e)
        {
        e.preventDefault();
        });
        });
        function populateTableData() {
        var data = '<%= request.getAttribute("data")%>';
        var jsonData = JSON.parse(data);
        var tableBody = $("#jobsTableBody");
        // $("#resultTable").DataTable({"scrollX": true});
//        $.each(jsonData, function(index, value) {
//
//        var tableRow = "<tr rowIndex=" + index + "> " + "<td id='jobNameRow'>" + value.artifactId + "</td>";
//        tableRow += "<td id='ProdRuntime1Row'><select class='select form-control' id='dropdown' rowIndex=" + index + " colIndex='1'></select></td>";
//        tableRow += "<td id='ProdRuntime2Row'><select class='select form-control' id='dropdown' rowIndex=" + index + " colIndex='2'></select></td>";
//        tableRow += "<td id='TestRuntime1Row'><select class='select form-control' id='dropdown' rowIndex=" + index + " colIndex='3'></select></td>";
//        tableRow += "<td id='TestRuntime2Row'><select class='select form-control' id='dropdown' rowIndex=" + index + " colIndex='4'></select></td>";
//        tableRow += "<td id='TestRuntime3Row'><select class='select form-control' id='dropdown' rowIndex=" + index + " colIndex='5'></select></td>";
//        tableRow += "</tr>";
//        tableBody.append(tableRow);
//        var selectElement = $("select[rowIndex='" + index + "']");
//        var versions = value.versions;
//        $.each(versions, function(ind, value) {
//        selectElement.append($("<option />").val(value).text(value));
//            });
//        });
        var table = $('#resultTable').DataTable({
            data: jsonData,
            ordering: false,
		columns: [
                    {
                        data : "artifactId"
                    },
                    {
                        data : "groupId"
                    },
                    { data: "versions", 
                      render: function (data, type, row, meta) {
                        var $select = $("<select class='select form-control' rowIndex=" + meta.row + " colIndex=" + meta.col + "></select>",
                            {
                                id: row.artifactId
                            });
                        $.each(data, function (k, v) {
                            var $option = $("<option></option>",
                            {
                                text: v,
                                value: v
                            });
                          $select.append($option);
                        });
                        return $select.prop("outerHTML");
                      }
                    },
                    { data: "versions", 
                      render: function (data, type, row, meta) {
                        var $select = $("<select class='select form-control' rowIndex=" + meta.row + " colIndex=" + meta.col + "></select>",
                          {
                            id: row.artifactId
                          });
                        $.each(data, function (k, v) {
                            var $option = $("<option></option>",
                            {
                                text: v,
                                value: v
                            });
                            $select.append($option);
                        });
                        return $select.prop("outerHTML");
                      }
                    },
                    { data: "versions", 
                      render: function (data, type, row, meta) {
                        var $select = $("<select class='select form-control' rowIndex=" + meta.row + " colIndex=" + meta.col + "></select>",
                          {
                            id: row.artifactId
                          });
                        $.each(data, function (k, v) {
                            var $option = $("<option></option>",
                            {
                                text: v,
                                value: v
                            });
                            $select.append($option);
                        });
                        return $select.prop("outerHTML");

                      }
                    
                    },
                    { data: "versions",  
                      render: function (data, type, row, meta) {
                        var $select = $("<select class='select form-control' rowIndex=" + meta.row + " colIndex=" + meta.col + "></select>",
                          {
                            id: row.artifactId
                          });
                        $.each(data, function (k, v) {
                            var $option = $("<option></option>",
                            {
                                text: v,
                                value: v
                            });
                            $select.append($option);

                        });
                        return $select.prop("outerHTML");
                      }
                    },
                    { data: "versions",  
                      render: function (data, type, row, meta) {
                        var $select = $("<select class='select form-control' rowIndex=" + meta.row + " colIndex=" + meta.col + "></select>",
                          {
                            id: row.artifactId
                          });
                        $.each(data, function (k, v) {
                            var $option = $("<option></option>",
                            {
                                text: v,
                                value: v
                            });
                            $select.append($option);
                        });
                        return $select.prop("outerHTML");
                      }
                    }
		]

            
          });
        
     }
    </Script>
</html>

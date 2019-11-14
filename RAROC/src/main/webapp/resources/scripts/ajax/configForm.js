/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(function () {

    $('[data-toggle="tooltip"]').tooltip();

    $('#groupBy').multiselect({
        buttonWidth: '100%'
    });

    $('#build').bind('click', function () {
        return hs.htmlExpand(this, popupConfig);
    });

    var popupConfig = {
        objectType: 'ajax',
        height: 600,
        width: 900,
        align: 'center',
        dimmingOpacity: 0.60,
        headingText: 'Expression / Data Column for ' + $("#shortName").text(),
        src: 'ewiConfig/popup/column/' + $("#id").val()
    };

    $(document).on("click", '#saveExpression', function (event) {
        $("#dataColumn").val($("#expression").val());
        hs.close(this);
    });

    $("#form").submit(function (e) {
        e.preventDefault();
        
        if($("#thresholdCondition").val() === 'NOT BETWEEN' || $("#thresholdCondition").val() === 'BETWEEN'){
            $("#thresholdValue").val($("#tFrom").val() + ' AND ' + $("#tTo").val());
        }
        
        $.ajax({
            type: 'POST',
            url: $(this).attr('action'),
            data: $(this).serialize(),
            global: false,
            async: false,
            success: function (text) {
                if (text === "success") {
                    location.reload();
                } else {
                    // Todo
                }
            }
        });
    });
    
    if ($("#thresholdCondition").val() === "BETWEEN" || $("#thresholdCondition").val() === "NOT BETWEEN") {
        var vals = $("#thresholdValue").val().split("AND");
        $("#tFrom").val(vals[0].trim());
        $("#tTo").val(vals[1].trim());
    } 
    
    $("#thresholdCondition").change(function() {       
       if ($(this).val() === "BETWEEN" || $(this).val() === "NOT BETWEEN") {
            $("#sInput").removeClass("show").addClass("hidden");
            $("#dInput").removeClass("hidden").addClass("show");
            var vals = $("#thresholdValue").val().split("AND");
            $("#tFrom").val(vals[0].trim());
            $("#tTo").val(vals[1].trim());
       } else {
            $("#dInput").removeClass("show").addClass("hidden");
            $("#sInput").removeClass("hidden").addClass("show");
       }
    });

    /*
     $.getJSON(path + "result", function (data) {
     var html = '<option selected value="">Select...</option>';
     var len = data.length;
     for (var i = 0; i < len; i++) {
     html += '<option value="' + data[i].code + '">' + data[i].desc + '</option>';
     }
     $("#table6").append(html);
     });
     */

});


jQuery.fn.extend({
    insertAtCaret: function (myValue) {
        return this.each(function (i) {
            if (document.selection) {
                //For browsers like Internet Explorer
                this.focus();
                var sel = document.selection.createRange();
                sel.text = myValue;
                this.focus();
            } else if (this.selectionStart || this.selectionStart === '0') {
                //For browsers like Firefox and Webkit based
                var startPos = this.selectionStart;
                var endPos = this.selectionEnd;
                var scrollTop = this.scrollTop;
                this.value = this.value.substring(0, startPos) + myValue + this.value.substring(endPos, this.value.length);
                this.focus();
                this.selectionStart = startPos + myValue.length;
                this.selectionEnd = startPos + myValue.length;
                this.scrollTop = scrollTop;
            } else {
                this.value += myValue;
                this.focus();
            }
        });
    }
});

hs.Expander.prototype.onAfterExpand = function (sender) {

    $("#cTree").jstree({
        "json_data": {
            "ajax": {
                "url": function (node) {
                    return "ewiConfig/tree/node/table?node=" + $("#dataTable").val();
                },
                "type": "get",
                "success": function (ops) {
                    var data = [], nodes = new Array(), state = ops.state, i = 0, node;
                    nodes = ops.nodes;
                    if (state === "parent") {
                        for (i = 0; i < nodes.length; i++) {
                            node = {
                                "data": nodes[i].value,
                                "metadata": {
                                    "key": nodes[i].key
                                },
                                "attr": {
                                    "rel": "parent"
                                },
                                "state": "closed"
                            };
                            data.push(node);
                        }
                    } else {
                        for (i = 0; i < nodes.length; i++) {
                            node = {
                                "data": nodes[i].value,
                                "metadata": {
                                    "key": nodes[i].key,
                                    "title": nodes[i].value
                                },
                                "attr": {
                                    "rel": "leaf"
                                }
                            };
                            data.push(node);
                        }
                    }
                    return data;
                }
            }
        },
        "types": {
            "valid_children": ["root"],
            "types": {
                "parent": {
                    "icon": {
                        "image": "resources/css/images/jsTree-db.png"
                    },
                    "valid_children": ["default"]
                },
                "leaf": {
                    "icon": {
                        "image": "resources/css/images/jsTree-point.png"
                    },
                    "valid_children": ["default"]
                }
            }
        },
        "core": {
            "html_titles": true,
            "load_open": true
        },
        "plugins": ["themes", "json_data", "ui", "cookies", "hotkeys", "types"]
    })
            .bind("select_node.jstree", function (event, data) {
                if (data.inst.is_leaf(data.args[0])) {
                    var column = data.rslt.obj.data("title"), parent = new Array(), i = 0;
                    data.rslt.obj.parents("li").each(function () {
                        parent[i] = $(this).children("a").text();
                        i++;
                    });
                    $("#expression").insertAtCaret(column);
                } else {
                    data.inst.toggle_node(data.rslt.obj);
                }
            });

    $("#oTree").jstree({
        "json_data": {
            "ajax": {
                "url": function (node) {
                    if (node === -1) {
                        return "ewiConfig/tree/op";
                    } else {
                        return "ewiConfig/tree/node/op?node=" + node.data("key");
                    }
                },
                "type": "get",
                "success": function (ops) {
                    var data = [], nodes = new Array(), state = ops.state, i = 0, node;
                    nodes = ops.nodes;
                    if (state === "parent") {
                        for (i = 0; i < nodes.length; i++) {
                            node = {
                                "data": nodes[i].value,
                                "metadata": {
                                    "key": nodes[i].key
                                },
                                "attr": {
                                    "rel": "root"
                                },
                                "state": "closed"
                            };
                            data.push(node);
                        }
                    } else {
                        for (i = 0; i < nodes.length; i++) {
                            node = {
                                "data": nodes[i].value,
                                "metadata": {
                                    "key": nodes[i].key
                                },
                                "attr": {
                                    "rel": "leaf"
                                }
                            };
                            data.push(node);
                        }
                    }
                    return data;
                }
            }
        },
        "types": {
            "valid_children": ["root"],
            "types": {
                "root": {
                    "icon": {
                        "image": "resources/css/images/jsTree-fx.png"
                    },
                    "valid_children": ["default"]
                },
                "leaf": {
                    "icon": {
                        "image": "resources/css/images/jsTree-point.png"
                    },
                    "valid_children": ["default"]
                }
            }
        },
        "core": {
            "html_titles": true,
            "load_open": true
        },
        "plugins": ["themes", "json_data", "ui", "cookies", "hotkeys", "types"]
    }).bind("select_node.jstree", function (event, data) {
        if (data.inst.is_leaf(data.args[0])) {
            var op = $.trim(data.rslt.obj.find('a').first().text());
            $("#expression").insertAtCaret(op);
        } else {
            data.inst.toggle_node(data.rslt.obj);
        }
    });
};   
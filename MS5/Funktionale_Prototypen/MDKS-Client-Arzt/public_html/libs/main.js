$(document).ready(function(){
    center();
});

function center(){
    
    $("#div_main").append('<div id="accordion"><h3>First header</h3><div>First content panel</div><h3>Second header</h3><div>Second content panel</div></div>');
    $("#accordion").accordion({
        active: 2
    });
    $('#accordion').accordion({defaultOpen: 'some_id'});
}
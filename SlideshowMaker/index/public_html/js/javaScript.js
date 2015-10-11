
 var i = 0;
    var slides;
    var Captions;
    var image;
    run({
         $(document).ready(function() {
           // window.onload=(function(event){
               $.getJSON('./json/Blank.json', function(jd) {
             //     $('#Title').html(jd.title);
            //       slides = jd.slides
            //       Captions = jd.SlideEditView
            //      if(slides[i].image_file_name == null){
            //          i = 0;
           //       }
                $('#Picture').attr("src", "img/"+slides[i].image_file_name);
                 $('#Caption').html(Captions[i].CAPTION);
               });
        //  });
       //  });

         });
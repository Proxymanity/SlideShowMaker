var i = 0;
    var slides;
    var Captions;
    var image;
    var x = 0;
         $(document).ready(function() {
            window.onload=(function(event){
               $.getJSON('json/' + Name, function(jd) {
                  $('#Title').html(jd.title);
                   slides = jd.slides;
                   Captions = jd.SlideEditView;
                  if(slides[i].image_file_name == null){
                      i = 0;
                  }
                $('#Picture').attr("src", "img/"+slides[i].image_file_name);
                 $('#Caption').html(Captions[i].CAPTION);
				 
               });
            });
         });
 function Next(){
     i++;
                  if(slides.length == i){
                      i = 0;
                  }
               
                $('#Picture').attr("src", "img/"+slides[i].image_file_name);
                 $('#Caption').html(Captions[i].CAPTION);
              
 }
 
  function Prev(){
     i--;
                  if(i < 0){
                      i = (slides.length -1);
                  }
                $('#Picture').attr("src", "img/"+slides[i].image_file_name);
                 $('#Caption').html(Captions[i].CAPTION);
               
           
 }
 function play(){
     $('#Play').attr("src", "img/icons/Pause.png");
     $('#PlayButton').attr("onclick", "Pause()");
     x = 0;
      myLoop();
 }   
 
 function myLoop () {           
   setTimeout(function () {    
      if (x == 0) {            
        Next();          
        myLoop();              
      }                       
   }, 3000)
}
    
 function Pause(){
     x = 1;
        $('#Play').attr("src", "img/icons/Play.png");
         $('#PlayButton').attr("onclick", "play()");
 }
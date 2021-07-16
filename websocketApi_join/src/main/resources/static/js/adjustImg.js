var oImg = document.querySelector(".box img");
var eleImg = document.querySelector('.box');
var store = {
    scale: 1
};

eleImg.onclick=function(){
    eleImg.style.display="none"
    oImg.src=''
}


$(function(){
	$("#message").delegate("img","click",function(){
        eleImg.style.display="flex";
        oImg.src=this.src;
	})	
})   



// 縮放處理
eleImg.addEventListener('touchstart', function (event) {
	console.log("touchstart進入");
	
	if(event.touches.length<2){
		event.stopImmediatePropagation(); // 阻止冒泡
		return;	  		
	}
    var touches = event.touches;
    var events = touches[0];
    var events2 = touches[1];
    if (!events) {
        return;
    }


    // 第一個觸摸點的座標
    store.pageX = events.pageX;
    store.pageY = events.pageY;

    store.moveable = true;

    if (events2) {
        store.pageX2 = events2.pageX;
        store.pageY2 = events2.pageY;
    }     
    store.originScale = store.scale || 1;
});
    
eleImg.addEventListener('touchmove', function (event) {    	
	if(event.touches.length==2){
		console.log("touchmove觸發2");
  
        if (!store.moveable) {
        	console.log("直接return");
            return;
        }
        let touches = event.touches;
        let events = touches[0];
        let events2 = touches[1];
        if (events2) {
        	// 雙指移動
            if (!store.pageX2) {
                store.pageX2 = events2.pageX;
            }
            if (!store.pageY2) {
                store.pageY2 = events2.pageY;
            }

            //獲取座標之間的舉例
            let getDistance = function (start, stop) {
                return Math.hypot(stop.x - start.x, stop.y - start.y);
            };

            let zoom = getDistance({
                x: events.pageX,
                y: events.pageY
            }, {
                x: events2.pageX,
                y: events2.pageY
            }) /
            getDistance({
                x: store.pageX,
                y: store.pageY
            }, {
                x: store.pageX2,
                y: store.pageY2
            });

            let newScale = store.originScale * zoom;
            // 最大缩放比例限制
            if (newScale > 3) {
                newScale = 3;
            }
            // 記住使用的缩放值
            store.scale = newScale;
            // 图像应用缩放效果                
            document.querySelector(".box img").style.transform = 'scale('+ newScale +')';    		
		}
	}
});

eleImg.addEventListener('touchend', function () {
    store.moveable = false;
    delete store.pageX2;
    delete store.pageY2;
});
    
eleImg.addEventListener('touchcancel', function () {
    store.moveable = false;
    delete store.pageX2;
    delete store.pageY2;
});    
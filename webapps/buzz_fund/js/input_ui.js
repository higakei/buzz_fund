/* ----------------------------------------------------------
	JS Information

	File name    : input_ui.js
	Style Info   : input.htmlのビヘイビア機能用のスクリプト
	Last update  : 2008-05-02 by Hirosuke Asano
	Author       : Hirosuke Asano (admin)
	Copyright    : (C) 2008 Hottolink.Inc
----------------------------------------------------------- */
var yearRange = '2007:'+ (new Date()).getFullYear();


if($.datepicker != undefined){
	//日本語カレンダーの初期化と幾つかの初期化
	$.datepicker.regional['ja'] = {
	 showOn: 'both',
	 buttonImageOnly: true, 
	 buttonImage: 'js/datepicker/input_btn01.gif',
	 buttonText: 'Calendar',
	 clearText: '削除',
	 clearStatus: '',
	 closeText: '閉じる',
	 closeStatus: '',
	 prevText: '<前月',
	 prevStatus: '',
	 nextText: '次月>',
	 nextStatus: '',
	 currentText: '今日',
	 currentStatus: '',
	 monthNames: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
	 monthNamesShort: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
	 monthStatus: '',
	 yearStatus: '',
	 weekHeader: 'Wk',
	 weekStatus: '',
	 dayNames: ['日','月','火','水','木','金','土'],
	 dayNamesShort: ['日','月','火','水','木','金','土'],
	 dayNamesMin: ['日','月','火','水','木','金','土'],
	 dayStatus: 'DD',
	 dateStatus: 'D, M d',
	 dateFormat: 'yy-mm-dd',
	 firstDay: 0,
	 initStatus: '',
	 speed:"fast",
	 closeAtTop:false,
	 isRTL: false,
	 yearRange:yearRange,
	 minDate:new Date(2007,0,1),
	 maxDate:new Date()
	 };

	$.datepicker.setDefaults($.datepicker.regional['ja']);

	//開始と終了のある日付入力フォームにカレンダーを追加します。
	//rangeclass:開始と終了のinput要素に同じIDを指定してください。
	//from_id:開始日のinput要素のidを指定してください。
	//to_id:終了日のinput要素のidを指定してください。
	function setDoubleCalender(dclass, from_id, to_id){
		
		$('.'+dclass).datepicker({
		beforeShow:function customRange(input) {
		    	return {minDate: (input.id == to_id ? $('#'+from_id).datepicker('getDate') : null),
	        	maxDate: (input.id == from_id ? $('#'+to_id).datepicker('getDate') : null)}; 
	        }
		});
	};

	function setSingleCalender(dclass){
		$('.'+dclass).datepicker();
	};
}


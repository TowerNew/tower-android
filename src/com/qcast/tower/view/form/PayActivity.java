package com.qcast.tower.view.form;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.qcast.tower.R;
import com.qcast.tower.thirdparty.alipay.PayResult;
import com.qcast.tower.thirdparty.alipay.SignUtils;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 支付界面
 */
@ResourceView(id = R.layout.activity_pay)
public class PayActivity extends ActivityEx {
	public final static int RESULT_SUCCESS = 0;
	public final static int RESULT_CANCEL = -1;
	public final static int RESULT_FAIL = -2;
	
	@ResourceView(id = R.id.pay_label_name)
	public TextView labName;
	@ResourceView(id = R.id.pay_label_description)
	public TextView labDescription;
	@ResourceView(id = R.id.pay_label_price)
	public TextView labPrice;
	@ResourceView(id = R.id.pay_button_confirm)
	public Button btnConfirm;
	@ResourceView(id = R.id.pay_button_cancel)
	public Button btnCancel;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
					status = RESULT_SUCCESS;
				}
				else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if(TextUtils.equals(resultStatus, "8000")) {
						// Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
						status = RESULT_FAIL;
					}
					else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						// Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
						status = RESULT_FAIL;
					}
				}
				PayActivity.this.finish();
				break;
			}
			default:
				break;
			}
		};
	};

	// 商户PID
	public static final String PARTNER = "2088121767853413";
	// 商户收款账号
	public static final String SELLER = "15915841463@163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKra80V4CvoG2EX+Ra7p6eaY1J62tS+5K0GtOMi9fdQWijcs3deHEy2dXWfxVNNWo7uO05k0ujoMzpc7C/IRWLSXB0kzP4asgJ7eaZP+t6o6S2hQZk5zudV3Uv8fGByJIWrxzgq79Q1bh/Set3LvMG4wjCcwDGmkP3SvFHx01BWlAgMBAAECgYA9XxqlaIJg31aOhdCYDPP03g75191rgBMWq1dLXC23okURnq8tPRrZdr5XmOuEYom71IkTbko6keEfl5kp4as/1ILYMMQkeOxtVd/91GUoyOdHFXRmYlv5OfBnuaKwNG15giKVjyxxmM/rjTK6+wgH45cs+IV/12TVWuCDs5h9wQJBAOMMqKnu0s0Eebg21aADpR37dOmkLWa1OJT+djZnqHx28KT7f8H6kdUHQnQE+Mf+2v8hM3EzVFgc9W7fv4yTr3UCQQDApAC/pC9HGYJfzvucmg9rSIYG8ot2PLg5UgxUHYnibqr6r2vNL42jyFLDIocNkqden3s/MkBXJLfeCsFoe7dxAkEAqqUQ/ucoOD5s3S8ZT7JtnyMms5NZLCB37kNxxWITF7itFNKRKtGQWXOjVl+GO4ooPihN6X6SncbmD/bKmHVHIQJBAL/4b1qK5iZHUHnjjS/xAt6Zhh6UQ9BPEBLnJawJR6fjOvXYLGhsNAz8CyW0Wbt34txTt4ExtZZh2+0k8LoLV7ECQDmrctr7ejf7hMbKESC472ViJAlZ6RV8huO7pkdp1gc7TEV/geeeYra+leFYrnFSmWpxFSJKXu4mwOUo7N+V+hs=";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	// 消息ID
	private static final int SDK_PAY_FLAG = 1;

	/**
	 * 传递的参数
	 */
	private String productName;
	private String productDescription;
	private int productPrice;
	
	private int status = RESULT_CANCEL;
	private int commandId = 0;

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 界面处理
		prepare();
	}
	
	/**
	 * 界面销毁
	 */
	@Override
    protected void onDestroy() {
		Intent intent = new Intent();
		PayActivity.this.setResult(status, intent);
		if(0 != commandId) {
			Controller.doMerge(commandId, status);
		}
		super.onDestroy();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		commandId =  this.getIntent().getIntExtra("commandId", 0);
		productName = this.getIntent().getStringExtra("name");
		if(null == productName) {
			productName = "";
		}
		productDescription = this.getIntent().getStringExtra("description");
		if(null == productDescription) {
			productDescription = "";
		}
		productPrice = this.getIntent().getIntExtra("price", 0);
		//
		labName.setText(productName);
		labDescription.setText(productDescription);
		labPrice.setText(String.valueOf((float)(productPrice / 100.0)) + "元");
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pay();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PayActivity.this.finish();
			}
		});
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							//
							finish();
						}
					}).show();
			return;
		}
		String orderInfo = getOrderInfo(productName, productDescription, String.valueOf(productPrice / 100));
		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + Host.fetchURL("PayNotify") + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
}

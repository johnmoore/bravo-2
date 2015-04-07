from flask import Flask
from flask import jsonify
from flask import make_response, request

app = Flask(__name__)


class InvalidRequestError(Exception):
	pass


@app.route("/listing/", methods=['GET'])
def list_apks_for_device():
    if 'deviceid' not in request.args:
    	raise InvalidRequestError()
    print "Received device ID: " + request.args['deviceid']
    all_apks = [{'apkname':'obscure.apk', 'servicetostart':'edu.bu.ec700.john.obscuremodule/edu.bu.ec700.john.obscuremodule.OverlayService', 'config':[{'action':'edu.bu.ec700.john.action.obscuremodule', 'resend':True, 'extras':{'type': 1, 'filterstring':'newfilterstr'}}]}, {'apkname':'censor.apk', 'servicetostart':'edu.bu.ec700.john.censormodule/edu.bu.ec700.john.censormodule.CensorService', 'config':[{'action':'edu.bu.ec700.john.action.censormodule', 'resend':True, 'extras':{'type': 1, 'filterstring':'newfilterstr'}}]}]
    return make_response(jsonify({'apks': all_apks}), 200)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=1337, threaded=True)

# TODO: Add database based on device ID
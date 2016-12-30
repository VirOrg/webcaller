# webcaller
This is the open source library for easing up the process of making http/https web call from Java and Android client. The underlying Http client library is OkHttp v3.4.1.
The salient features of the library: 
1. A custom stack of each and every web call is being managed in a Hash Map with respect to a key. Key is basically a tag, which is always associated with each and every call. One biggest question which comes in mind, why are we doing so?
Please consider the following situation:
Suppose user makes web call on the UI and unfortunately it fails due network error. UI is having retry button through which that call can be again made. In that situation, you can get complete request object from tag, which is previously attached with call and you can again make a call with the older object. Please consider sample for more information.
2. A web call can easily be canceled.
3. A completely well parsed Java POJO is sent in response of the web call
4. This library eases up the process of multipart data request over network.

If you have any query and suggestion, you can mail me on droiddev2017@gmail.com. You can also contribute on further development via sending pull request.

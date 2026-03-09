package io.appwrite.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OAuthProvider(val value: String) {
    @SerialName("amazon") Amazon("amazon"),
    @SerialName("apple") Apple("apple"),
    @SerialName("auth0") Auth0("auth0"),
    @SerialName("authentik") Authentik("authentik"),
    @SerialName("bitbucket") Bitbucket("bitbucket"),
    @SerialName("bitly") Bitly("bitly"),
    @SerialName("box") Box("box"),
    @SerialName("dailymotion") Dailymotion("dailymotion"),
    @SerialName("discord") Discord("discord"),
    @SerialName("disqus") Disqus("disqus"),
    @SerialName("dropbox") Dropbox("dropbox"),
    @SerialName("etsy") Etsy("etsy"),
    @SerialName("facebook") Facebook("facebook"),
    @SerialName("github") GitHub("github"),
    @SerialName("gitlab") GitLab("gitlab"),
    @SerialName("google") Google("google"),
    @SerialName("linkedin") LinkedIn("linkedin"),
    @SerialName("microsoft") Microsoft("microsoft"),
    @SerialName("notion") Notion("notion"),
    @SerialName("oidc") Oidc("oidc"),
    @SerialName("okta") Okta("okta"),
    @SerialName("paypal") PayPal("paypal"),
    @SerialName("slack") Slack("slack"),
    @SerialName("spotify") Spotify("spotify"),
    @SerialName("stripe") Stripe("stripe"),
    @SerialName("tradeshift") Tradeshift("tradeshift"),
    @SerialName("twitch") Twitch("twitch"),
    @SerialName("wordpress") WordPress("wordpress"),
    @SerialName("yahoo") Yahoo("yahoo"),
    @SerialName("zoom") Zoom("zoom"),
}

@Serializable
enum class AuthenticationFactor(val value: String) {
    @SerialName("email") Email("email"),
    @SerialName("phone") Phone("phone"),
    @SerialName("totp") Totp("totp"),
    @SerialName("recoverycode") RecoveryCode("recoverycode"),
}

@Serializable
enum class ImageGravity(val value: String) {
    @SerialName("center") Center("center"),
    @SerialName("top-left") TopLeft("top-left"),
    @SerialName("top") Top("top"),
    @SerialName("top-right") TopRight("top-right"),
    @SerialName("left") Left("left"),
    @SerialName("right") Right("right"),
    @SerialName("bottom-left") BottomLeft("bottom-left"),
    @SerialName("bottom") Bottom("bottom"),
    @SerialName("bottom-right") BottomRight("bottom-right"),
}

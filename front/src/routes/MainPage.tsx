import CommonDiv from "../components/mainpage/CommonDiv";
import MainDiv from "../components/MainDiv";
import Top8 from "../components/mainpage/Top8";
import TrendKeyword from "../components/mainpage/TrendKeyword";
import TrendNews from "../components/mainpage/TrendNews";

function MainPage() {
  return (
    <>
      <MainDiv style={{ marginBottom: "2vh" }}>
        <CommonDiv
          type={true}
          firstWord="랭킹"
          secondWord="Top 8"
          ratio="0.7"
          mr={true}
          data={Top8()}
        />
        <CommonDiv
          type={false}
          firstWord="인기"
          secondWord="키워드"
          ratio="0.3"
          data={<TrendKeyword />}
        />
      </MainDiv>
      <MainDiv>
        <CommonDiv
          type={false}
          firstWord="트렌딩"
          secondWord="뉴스"
          ratio="0.47"
          mr={true}
          data={<TrendNews />}
        />
        <CommonDiv
          type={false}
          firstWord="트렌딩"
          secondWord="유튜브"
          ratio="0.53"
          data={<TrendNews />}
        />
      </MainDiv>
    </>
  );
}

export default MainPage;

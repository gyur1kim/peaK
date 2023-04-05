import CarouselCustom from "../components/Carousel/CarouselCustom.jsx";
import CommonDiv from "../components/MainPage/CommonDiv";
import MainDiv from "../components/MainDiv";
import NameDiv from "../components/NameDiv";
import ReactGA from 'react-ga';
import Top8 from "../components/MainPage/Top8";
import TrendKeyword from "../components/MainPage/TrendKeyword";
import { TrendNewsListType } from "../_utils/Types.js";
import axios from "axios";
import styled from "styled-components";
import { useEffect } from "react";
import { useLoaderData } from "react-router";

const BASE_URL = process.env.REACT_APP_BASE_URL;

export async function loader() {
  let TrendNewsList,
    TrendYoutubeList = null;

  await axios
    .get(`${BASE_URL}news/list/all-idol`)
    .then(response => {
      console.log(response.data);
      TrendNewsList = response.data;
    })
    .catch(error => console.log(error));

  return [TrendNewsList];
  // return [TrendNewsList, TrendYoutubeList];
}

type CarouselCustomDivType = {
  ratio: number;
};

const CarouselDiv = styled.div`
  width: 100%;
  margin: 0 auto;
`;

const CarouselCustomDiv = styled.div<CarouselCustomDivType>`
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0 auto;
  flex: ${props => props.ratio};
`;

function MainPage() {
  const TrendNewsList = useLoaderData() as TrendNewsListType[];

  useEffect(() => {
    ReactGA.set({ userId: 'chohm1223@naver.com' });
  },[])
  // const TrendYoutubeList = [
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "YouTube에서 마음에 드는 동영상과 음악을 감상하고, 직접 만든 콘텐츠를 업로드하여 친구, 가족뿐..",
  //     src: "https://i.ytimg.com/vi/v_5JkyrTzEo/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLCSN0w6URSE6bwXzWcvnfNoRLcYvw",
  //   },
  //   {
  //     title: "(4K UHD) [최초공개] KF-21 무장분리・기..",
  //     content:
  //       "대한민국의 KF-21이 공군 비행단에서 이륙하여 남해 상공에서 공대공 무장분리 시험과 공중 .. ",
  //     src: "https://i.ytimg.com/vi/ff68QPAI6YI/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLB-eVctdC6EbDG7T7D0SHV5yCWYRw",
  //   },
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "YouTube에서 마음에 드는 동영상과 음악을 감상하고, 직접 만든 콘텐츠를 업로드하여 친구, 가족뿐..",
  //     src: "https://i.ytimg.com/vi/ff68QPAI6YI/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLB-eVctdC6EbDG7T7D0SHV5yCWYRw",
  //   },
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "대한민국의 KF-21이 공군 비행단에서 이륙하여 남해 상공에서 공대공 무장분리 시험과 공중 .. ",
  //     src: "https://i.ytimg.com/vi/v_5JkyrTzEo/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLCSN0w6URSE6bwXzWcvnfNoRLcYvw",
  //   },
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "YouTube에서 마음에 드는 동영상과 음악을 감상하고, 직접 만든 콘텐츠를 업로드하여 친구, 가족뿐..",
  //     src: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2F20101101_73%2Fjmclub2_1288617811796TklMy_jpg%2F246168185_f48ed70ffe_o_jmclub2.jpg&type=a340",
  //   },
  //   {
  //     title: "(4K UHD) [최초공개] KF-21 무장분리・기..",
  //     content:
  //       "대한민국의 KF-21이 공군 비행단에서 이륙하여 남해 상공에서 공대공 무장분리 시험과 공중 .. ",
  //     src: "https://i.ytimg.com/vi/ff68QPAI6YI/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLB-eVctdC6EbDG7T7D0SHV5yCWYRw",
  //   },
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "YouTube에서 마음에 드는 동영상과 음악을 감상하고, 직접 만든 콘텐츠를 업로드하여 친구, 가족뿐..",
  //     src: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2F20101101_73%2Fjmclub2_1288617811796TklMy_jpg%2F246168185_f48ed70ffe_o_jmclub2.jpg&type=a340",
  //   },
  //   {
  //     title: "4 시간 지브리 메들리 피아노 💖 ..",
  //     content:
  //       "대한민국의 KF-21이 공군 비행단에서 이륙하여 남해 상공에서 공대공 무장분리 시험과 공중 .. ",
  //     src: "https://i.ytimg.com/vi/ff68QPAI6YI/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLB-eVctdC6EbDG7T7D0SHV5yCWYRw",
  //   },
  // ];
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
        <CarouselCustomDiv ratio={0.47}>
          <NameDiv type={false} firstWord="트렌딩" secondWord="뉴스" />
          <CarouselDiv>
            <CarouselCustom items={TrendNewsList[0]} />
          </CarouselDiv>
        </CarouselCustomDiv>
        <CarouselCustomDiv ratio={0.47}>
          <NameDiv type={false} firstWord="트렌딩" secondWord="유튜브" />
          <CarouselDiv>{/* <CarouselCustom items={TrendYoutubeList} /> */}</CarouselDiv>
        </CarouselCustomDiv>
      </MainDiv>
    </>
  );
}

export default MainPage;
